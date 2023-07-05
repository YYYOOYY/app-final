package org.edupoll.model.dto.response;

import org.edupoll.model.entity.User;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginResponseData {
	String email;
	String password;
	
	public UserLoginResponseData(User entity) {
		this.email = entity.getEmail();
		this.password = entity.getPassword();
	}
}
