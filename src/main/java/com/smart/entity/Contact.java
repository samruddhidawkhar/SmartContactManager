package com.smart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "CONTACT")
public class Contact{
	
	@Id
	@GeneratedValue(generator = "contact_gen", strategy = GenerationType.AUTO)
	@SequenceGenerator(name = "contact_gen", sequenceName = "contact_seq", initialValue = 1, allocationSize = 1)
	@Column(name ="cid")
	private int cid;
	
	@Column(name ="name")
	private String name;
	
	@Column(name ="nickname")
	private String nickname;
	
	@Column(name ="work")
	private String work;
	
	@Column(name ="email")
	private String email;
	
	@Column(name ="image")
	private String image;
	
	@Column(name ="description")
	private String description;
	
	@Column(name ="phone")
	private String phone;
	
	@ManyToOne
	@JsonIgnore
	private User user;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
    @Transient
	public String getLogoImagePath() {
    	
    	if(image == null)
    		return "/img/defaultprofilephoto.avif";
		
		return "/profilephoto/" + name + "_" + phone + "/" + image ;
	}
}
