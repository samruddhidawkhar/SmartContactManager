package com.smart.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.smart.model.UserDetails;

@Service
public interface HomeService {
	
	public void addDataToDb(UserDetails userDetails);

}
