package com.smart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.model.UserDetails;

@Service
public class HomeServiveImpl implements HomeService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void addDataToDb(UserDetails userDetails) {
		// TODO Auto-generated method stub
		
		User user = new User();
		
		user.setName(userDetails.getName());
		user.setEmail(userDetails.getEmail());
		user.setAbout(userDetails.getAbout());
		user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
		user.setRole("ROLE_USER");
		user.setEnable(true);
		user.setImageUrl("userdefaultprofileimg.png");
		
		System.out.println("userDetails : " + userDetails);
		
		System.out.println("Adding data to DB");
		User result = this.userRepository.save(user);
		System.out.println("Data saved in db : " + result);
		
		
	}

}
