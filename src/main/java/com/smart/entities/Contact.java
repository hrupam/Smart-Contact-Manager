package com.smart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "contact")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;

	@NotBlank(message = "Name cannot be blank")
	@Size(min = 2, message = "Name must be minimum 2 characters")
	private String name;

	private String nickname;
	private String work;
	private String email;
	private String image;

	@Column(length = 1000)
	private String description;

	@NotBlank(message = "Phone number cannot be blank")
	private String phone;

	@ManyToOne
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

//	public String getImageUrl() {
//		return image;
//	}
//
//	public void setImageUrl(String imageUrl) {
//		this.image = imageUrl;
//	}

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

//	@Override
//	public String toString() {
//		return "Contact [cid=" + cid + ", name=" + name + ", nickname=" + nickname + ", work=" + work + ", email="
//				+ email + ", imageUrl=" + imageUrl + ", description=" + description + ", phone=" + phone + ", user="
//				+ user + "]";
//	}

}
