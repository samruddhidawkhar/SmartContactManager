package com.smart.service;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.smart.model.ContactDetails;
import com.smart.model.Passwords;

@Service
public interface UserService {
	
	public void addUserContactToDb(ContactDetails contactDetails,MultipartFile file,Model model);
	
	public void fetchContacts(Integer page,Model model, Principal principal);
	
	public void fetchContactDetails(Integer cId,Model model, Principal principal);
	
	public void deleteContact(Integer cId, Model model, Principal principal, HttpSession session);

	public void updateContactForm(Integer cId, Model model);
	
	public void updateContact(ContactDetails contactDetails,MultipartFile file,Model model,HttpSession session,Principal principal,Integer cid);

    public boolean changeUserPassword(Passwords passwords,Principal principal,HttpSession session);
    
    public String createOrderForPayment( Map<String,Object> data, Principal principal) throws Exception;
    
    public void updateOrder(Map<String, Object> data);
}
