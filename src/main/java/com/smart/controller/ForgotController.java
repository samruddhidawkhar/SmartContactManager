package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.service.EmailService;
import com.smart.utils.Message;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;

	//Random random = new Random(1000);
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}
	
	
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		
		System.out.println("*********************email : " + email);
		
		//int otp = random.nextInt(999999);
		
		int otp = (int) (Math.random()*900000 + 100000);
		
		System.out.println("*****************otp : " + otp);
		
		//write code for send otp to email...
		
		String subject = "OTP for verification of your SCM";
		String message = ""
				+ "<div style='border:1px solid #e2e2e2; padding:20px'>"
				+ "<h1>"
				+ "OTP - "
				+ "<b>"+otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		String to =email;
		
		boolean flag = this.emailService.sendEmail(message, subject, to);
		
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else {
			session.setAttribute("message","Check your email id!!");
			return "forgot_email_form";
		}
		
		
	}
	
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		
		int myotp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");
		
		if(myotp == otp) {
			
			User user = this.emailService.fetchingDataFromDB(email);
			
			if(user == null) {
				session.setAttribute("message", "User does not exits with this email id");
				
				return "forgot_email_form";
			}else {
				
				//send password change form
				
			}
			
			return "password_change_form";
		}else {
			session.setAttribute("message","You have entered wrong otp");
			return "verify_otp";
		}
		
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String email = (String) session.getAttribute("email");

		this.emailService.saveUpdatedPasswordToDB(email, newpassword);
		session.setAttribute("message", "Password is changed...");
		
		return "redirect:/signin";
		
	}

}
