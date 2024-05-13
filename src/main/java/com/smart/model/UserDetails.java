package com.smart.model;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserDetails {

	
	@NotBlank(message = "Name is mandatory")
	@JsonProperty("name")
	private String name;
	
	@NotBlank(message = "Email is mandatory")
	@Email(regexp = "^[a-zA-Z0-9.]+@[a-zA-Z0-9.]+$")
	@JsonProperty("email")
	private String email;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "password must contain atleast 1 uppercase, 1 lowercase, 1 special character, 1 digit and length should be greater then 8 and less then 20")
	@JsonProperty("password")
	private String password;
	
	
	@JsonProperty("about")
	private String about;

}
