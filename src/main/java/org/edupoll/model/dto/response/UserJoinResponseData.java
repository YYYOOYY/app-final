package org.edupoll.model.dto.response;

import org.edupoll.model.entity.User;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserJoinResponseData {
	
	Long id;
	String email;
	String password;
	String name;
	String profileImage;
	String social;
	
	public UserJoinResponseData(User entity) {
		this.id = entity.getId();
		this.email = entity.getEmail();
		this.password = entity.getPassword();
		this.name = entity.getName();
		this.profileImage = entity.getProfileImage();
		this.social = entity.getSocial();
	}
}
