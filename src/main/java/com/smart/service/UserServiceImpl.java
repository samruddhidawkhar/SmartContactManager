package com.smart.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.MyOrder;
import com.smart.entity.User;
import com.smart.model.ContactDetails;
import com.smart.model.Passwords;
import com.smart.utils.FileUploadUtil;
import com.smart.utils.Message;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private MyOrderRepository myOrderRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void addUserContactToDb(ContactDetails contactDetails,MultipartFile file,Model model) {
		
		User user = (User) model.getAttribute("user");
		
		
		Contact contact = new Contact();
		
		contact.setName(contactDetails.getName());
		contact.setNickname(contactDetails.getNickname());
		contact.setEmail(contactDetails.getEmail());
		contact.setDescription(contactDetails.getDescription());
		contact.setPhone(contactDetails.getPhone());
		contact.setWork(contactDetails.getWork());
		
		if(file.isEmpty()) {
			
			System.out.println("Image is not uploaded");
			
		}
		else {
			
			String uploadDir = "target/classes/static/profilephoto/" + contactDetails.getName() + '_' + contactDetails.getPhone();
			
			FileUploadUtil.folderUpload(contact, contactDetails, file, uploadDir);	
			
		}
		
		
		contact.setUser(user);
		
		user.getContacts().add(contact);
		System.out.println("Data Added in DB : " + contact);
		
		this.userRepository.save(user);
		
	}

	@Override
	public void fetchContacts(Integer page, Model model, Principal principal) {

         model.addAttribute("title","Show User Contacts");
		
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		
		int user_id = user.getId();
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = this.contactRepository.findContactByUser(user_id,pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
	   
		System.out.println("********************************contacts.getTotalElements()"+ contacts.getTotalElements());
		System.out.println("********************************contacts.getTotalPages()"+contacts.getTotalPages());
		
	}

	@Override
	public void fetchContactDetails(Integer cId, Model model, Principal principal) {
		
		Optional<Contact> findById = this.contactRepository.findById(cId); // need to handle exception here
		Contact contact = findById.get();
		
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		
		if(user.getId() == contact.getUser().getId()) {
			model.addAttribute("contactdetails",contact);
			model.addAttribute("title",contact.getName());
		}
		
	}

	
	//not able to delete child table records => following n-tier architecture and annotating with @Transactional at class level or method level will solve the issue 
	//(currently the issue is fix by another method)
	@Override
	public void deleteContact(Integer cId, Model model, Principal principal, HttpSession session) {

		Optional<Contact> findById = this.contactRepository.findById(cId);
		Contact contact = findById.get();
		
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		
		
		
		if(user.getId() == contact.getUser().getId()) {
			
			
			user.getContacts().remove(contact);
			
			this.userRepository.save(user);
			
			String uploadDir = "target/classes/static/profilephoto/" + contact.getName() + '_' + contact.getPhone();
			
			try {
				FileUtils.deleteDirectory(new File(uploadDir));
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			session.setAttribute("message", new Message("Contact deleted successfully...","success"));
		}
		
	}

	@Override
	public void updateContactForm(Integer cId, Model model) {
		
		Contact contact = this.contactRepository.findById(cId).get();
		
		model.addAttribute("contactDetails", contact);
		
		
		
	}

	@Override
	public void updateContact(ContactDetails contactDetails, MultipartFile file, Model model, HttpSession session,
			Principal principal,Integer cid) {
		
		Contact oldcontact = this.contactRepository.findById(cid).get();
		String image = oldcontact.getImage();
		
		Contact contact = new Contact();
		
		
		try {
			
			String uploadDir = "target/classes/static/profilephoto/" + contactDetails.getName() + '_' + contactDetails.getPhone();
			String uploadDirOld= "target/classes/static/profilephoto/" + oldcontact.getName() + '_' + oldcontact.getPhone();
			if(!file.isEmpty()) {
				

				
				
				FileUploadUtil.deleteDImageirectory(uploadDir);
				
				FileUploadUtil.folderUpload(contact, contactDetails, file, uploadDir);
				
		
				System.out.println("Image is uploaded");
				
			}else if(file.isEmpty() && image != null) {
				
				File oldFolder = new File(uploadDirOld);
				File newFolder = new File(uploadDir);
				
				  if (oldFolder.renameTo(newFolder)) {
				      System.out.println("Folder renamed successfully!");
				    } else {
				      System.out.println("Folder could not be renamed.");
				    }
				
				contact.setImage(image);
			}
			
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);
			
			contact.setCid(cid);
			contact.setName(contactDetails.getName());
			contact.setNickname(contactDetails.getNickname());
			contact.setEmail(contactDetails.getEmail());
			contact.setDescription(contactDetails.getDescription());
			contact.setPhone(contactDetails.getPhone());
			contact.setWork(contactDetails.getWork());
			
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			
			session.setAttribute("message", new Message("Your contact is updated...","success"));
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean changeUserPassword(Passwords passwords,Principal principal,HttpSession session) {
		
		String username = principal.getName();
		
		User currentUser = userRepository.getUserByUserName(username);
		
		if(this.passwordEncoder.matches(passwords.getOldPassword(), currentUser.getPassword())) {
			
			currentUser.setPassword(this.passwordEncoder.encode(passwords.getNewPassword()));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password changed","success"));
			
			return true;
		}else {
			session.setAttribute("message", new Message("Your old password is incorrect","danger"));
			return false;
		}
		
	}

	@Override
	public String createOrderForPayment( Map<String,Object> data, Principal principal) throws Exception{
		System.out.println("data : " + data);
		
		int amount = Integer.parseInt(data.get("amount").toString());
		
		
		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_QACBg8MrqlVJFn","FO8dK8HoOrFdsQ7P3C2ETAU1");
		
		JSONObject obj = new JSONObject();
		obj.put("amount", amount*100);
		obj.put("currency", "INR");
		obj.put("receipt", "txn_235425");
		
		//creating new Order
		
		Order order = razorpayClient.orders.create(obj);
		
		System.out.println("****************************order: " +order);
		
		//save the order in database
		MyOrder myOrder = new MyOrder();
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		this.myOrderRepository.save(myOrder);
		
		return order.toString();
	}

	@Override
	public void updateOrder(Map<String, Object> data) {
		
		MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myOrder);
		
	}
	

}
