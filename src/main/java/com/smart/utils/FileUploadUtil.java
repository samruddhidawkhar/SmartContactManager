package com.smart.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.smart.controller.UserController;
import com.smart.entity.Contact;
import com.smart.model.ContactDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUploadUtil {
	
	
	 public static void folderUpload(Contact contact,ContactDetails contactDetails,MultipartFile file,String uploadDir) {
		 
		    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			contact.setImage(fileName);

			try {
				FileUploadUtil.saveFile(uploadDir, fileName, file);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			log.info("**********************************************  Image is uploaded");
			
	 }

	 public static boolean saveFile(String uploadDir, String fileName,
	        MultipartFile multipartFile) throws IOException {
	        Path uploadPath = Paths.get(uploadDir);
	         
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }
	         
	        try (InputStream inputStream = multipartFile.getInputStream()) {
	            Path filePath = uploadPath.resolve(fileName);
	            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	            return true;
	        } catch (IOException ioe) {        
	          
	        	log.info("**********************************************  ioe.getMessage() : " + ioe.getMessage());
	        
	        }  
	        
	        return false;
	    }
	 
	 public static void deleteDImageirectory(String uploadDir) {
		 
		 try {
				FileUtils.deleteDirectory(new File(uploadDir));
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	 }
}
