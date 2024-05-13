package com.smart.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER")
public class User {

	@Id
	@GeneratedValue(generator = "user_gen", strategy = GenerationType.AUTO)
	@SequenceGenerator(name = "user_gen", sequenceName = "user_seq", initialValue = 1, allocationSize = 1)
	@Column(name ="id")
	private int id;
	
	@Column(name ="name")
	private String name;
	
	@Column(name ="email", unique = true)
	private String email;
	
	@Column(name ="password")
	private String password;
	
	@Column(name ="imageUrl")
	private String imageUrl;
	
	@Column(name ="about")
	private String about;
	
	@Column(name ="enable")
	private boolean enable;
	
	@Column(name ="role")
	private String role;
	
	@OneToMany(orphanRemoval=true,cascade = {CascadeType.PERSIST,CascadeType.MERGE},mappedBy = "user")
	private List<Contact> contacts = new ArrayList<>();
	
//	@OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.LAZY)
//	private List<Contact> contacts = new ArrayList<>();
	
	
	
}
