package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.model.UserDetails;
import com.smart.service.HomeService;
import com.smart.utils.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {
	
	@Autowired
	private HomeService homeService;
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("userDetails",new UserDetails());
		log.info("**********************************************  model.getAttribute(\"userDetails\") : " + model.getAttribute("userDetails"));
		
		return "signup";
	}
	
	/* /do_register */
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("userDetails") UserDetails userDetails,BindingResult bindingresult,@RequestParam(value = "agreement", defaultValue="false") Boolean agreement,Model model,HttpSession session) {
		
		log.info("**********************************************  model.getAttribute(\"userDetails\") : " + model.getAttribute("userDetails"));
		
		if(bindingresult.hasErrors()) {
			log.info("**********************************************  bindingresult : " + bindingresult);
		
			return "signup";
		}
		
		
		try {
			
			if(!agreement) {
				throw new Exception("You have not agreed the terms and conditions");
			}

			this.homeService.addDataToDb(userDetails);
			model.addAttribute("title","Successfully Registered");
			
			return "registered";
			
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("userDetails",userDetails);
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
	}
	
	
	@RequestMapping("/registered")
	public String registered(Model model) {
		
		model.addAttribute("title","Successfully Registered");
		return "registered";
	}

	@GetMapping("/signin")
    public String customLogin(Model model) {
		
		model.addAttribute("title","Login Page");
    	
    	return "login";
    }
}
