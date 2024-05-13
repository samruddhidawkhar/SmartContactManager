package com.smart.service;

import org.springframework.stereotype.Service;

import com.smart.entity.User;

@Service
public interface EmailService {

	public boolean sendEmail(String message, String subject, String to);
	
	public User fetchingDataFromDB(String email);
	
	public void saveUpdatedPasswordToDB(String email, String newpassword);
}
