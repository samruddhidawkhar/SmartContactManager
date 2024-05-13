package com.smart.service;

import java.util.Properties;


import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entity.User;


@Service
public class EmailServiceImpl implements EmailService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public boolean sendEmail(String message, String subject, String to) {
		
		boolean flag =false;
		
		String from ="teetasamruddhidawkhar@gmail.com";
		//Variable for gmail
				String host = "smtp.gmail.com";
				
				//get the system properties
				Properties properties = System.getProperties();
				System.out.print("properties : " + properties);
				
				//setting important information to properties object
				
				//host set
				properties.put("mail.smtp.host", host);
				properties.put("mail.smtp.port", "465");
				properties.put("mail.smtp.ssl.enable", "true");
				properties.put("mail.smtp.auth", "true");
				
				//step 1: to get the session object
				Session session = Session.getInstance(properties, new Authenticator() {

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						// TODO Auto-generated method stub
						return new PasswordAuthentication("teetasamruddhidawkhar@gmail.com","fwma jdki wiph iiva");
					}
					
				});
				
				session.setDebug(true);
				
				//Step 2 : compose the message[text, multi media]
				
				MimeMessage m = new MimeMessage(session);
				
				try {
					
					//from email
					m.setFrom(from);
					
					//adding recipient to message
					m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
					
					//adding subject to message
					m.setSubject(subject);
					
					//adding text to message
					//m.setText(message);
					m.setContent(message,"text/html");
					
					//Step 3 : send the message using Transport class
					Transport.send(m);
					
					System.out.println("Sent success.................");
					flag = true;
					
				}catch(Exception e) {
					
					e.printStackTrace();
				}
				
				return flag;
	}
	
	@Override
	public User fetchingDataFromDB(String email) {
		
		User user = this.userRepository.getUserByUserName(email);
		
		return user;
	}


	@Override
	public void saveUpdatedPasswordToDB(String email, String newpassword) {
        User user = fetchingDataFromDB(email);
		
		user.setPassword(this.passwordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
	}

	
}
