package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.model.ContactDetails;
import com.smart.model.Passwords;
import com.smart.service.UserService;
import com.smart.utils.FileUploadUtil;
import com.smart.utils.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		
		log.info("**********************************************  addCommonData");
		String username = principal.getName();
		System.out.println("USERNAME : " + username);
		
		User user = userRepository.getUserByUserName(username);
		
		System.out.println("user : " + user);
		
		model.addAttribute("user",user);
		
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		log.info("**********************************************  dashboard");
		model.addAttribute("title","User Dashboard");
		
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		log.info("**********************************************  openAddContactForm");
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contactDetails", new ContactDetails());
		
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String processContact(Model model,
								@RequestParam("profileImage") MultipartFile file,
								@Valid @ModelAttribute("contactDetails") ContactDetails contactDetails,
								BindingResult bindingresult,
								HttpSession session) {
		
		model.addAttribute("title","Add Contact");
		log.info("**********************************************   model.getAttribute(\"contact\") : " +  model.getAttribute("contact"));
		
	      if(bindingresult.hasErrors()) {
			
	    	log.info("**********************************************  bindingresult");
			session.setAttribute("message", new Message("Something went wrong !! Try again...","danger"));
			return "normal/add_contact_form";
		}
		
		try {

			this.userService.addUserContactToDb(contactDetails, file, model);
			session.setAttribute("message", new Message("Your contact is added !! Add more...","success"));
			
		}catch(Exception e) {
			
			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();
		}
		
		
		model.addAttribute("contactDetails", new ContactDetails());
		System.out.println("Contact - Details : " + contactDetails);
		
		return "normal/add_contact_form";
	}
	
	//show contacts handler
	@GetMapping("/show-contact/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model, Principal principal) {
		
        this.userService.fetchContacts(page, model, principal);
		return "normal/show_contacts";
	}
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId,Model model, Principal principal) {
		
		this.userService.fetchContactDetails(cId, model, principal);
	
		return "normal/contact_detail";
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal, HttpSession session) {
		
		this.userService.deleteContact(cId, model, principal, session);
		
		return "redirect:/user/show-contact/0";
	}
	
	//open contact handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cid, Model m) {
		
		m.addAttribute("title","Update Contact");
		
		this.userService.updateContactForm(cid, m);
		
		return "normal/update_form";
	}
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute("contactDetails") ContactDetails contactDetails
								,@RequestParam("profileImage") MultipartFile file
								,Model model
								,HttpSession session
								,Principal principal
								,@RequestParam("cid") Integer cid) {
		
		this.userService.updateContact(contactDetails, file, model, session, principal,cid);
		
		return "redirect:/user/"+cid+"/contact";
	}
	
	//pofile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "Profile Page");
		
		return "normal/profile";
	}
	
	
	//open settings handler
	@GetMapping("/settings")
	public String openSettings(Model model) {
		
		model.addAttribute("title", "Change password here");
		model.addAttribute("passwords", new Passwords());
		
		log.info("**********************************************  openSettings");
		return "normal/settings";
	}
	
	
	@PostMapping("/change-password")
	public String changePassword(@Valid @ModelAttribute("passwords") Passwords passwords,BindingResult bindingresult,Principal principal,HttpSession session,Model model) {
		
		log.info("**********************************************  newPassword : " + passwords.getNewPassword());
		log.info("**********************************************  oldPassword : " + passwords.getOldPassword());
		 if(bindingresult.hasErrors()) {
				
			 log.info("**********************************************  bindingresult");
				model.addAttribute("title", "Enter valid password");
				return "normal/settings";
			}
		
		boolean changeUserPassword = this.userService.changeUserPassword(passwords, principal, session);
		
		if(changeUserPassword) {
			model.addAttribute("title", "Password changed");
		}else {
			model.addAttribute("title", "Old password is incorrect");
		}
		
		return "normal/settings";
	}
	
	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String,Object> data, Principal principal) throws Exception {
		
		String createOrderForPayment = this.userService.createOrderForPayment(data, principal);
		
		return createOrderForPayment;
	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
		
		this.userService.updateOrder(data);
		
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
	

}
