package com.smart.model;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Passwords {
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "password must contain atleast 1 uppercase, 1 lowercase, 1 special character, 1 digit and length should be greater then 8 and less then 20")
	@JsonProperty("oldPassword")
	private String oldPassword;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "password must contain atleast 1 uppercase, 1 lowercase, 1 special character, 1 digit and length should be greater then 8 and less then 20")
	@JsonProperty("newPassword")
	private String newPassword;

}
