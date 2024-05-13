package com.smart.model;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smart.entity.User;

import lombok.Data;

@Data
public class ContactDetails {
	
	@NotBlank(message = "Name is mandatory")
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("nickname")
	private String nickname;
	
	@JsonProperty("work")
	private String work;
	
	@Email(regexp = "^[a-zA-Z0-9.]+@[a-zA-Z0-9.]+$")
	@JsonProperty("email")
	private String email;
	
//	@Pattern(regexp = "([^\\s]+(\\.(?i)(jpe?g|png))$)",
//            message = "Upload only jpg, png or JPEG files")
//	@JsonProperty("profileImage")
//	private String image;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("phone")
	private String phone;

}
