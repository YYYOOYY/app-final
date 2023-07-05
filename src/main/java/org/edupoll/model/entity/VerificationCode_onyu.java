package org.edupoll.model.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "verificationCodes")
public class VerificationCode_onyu {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String code;
	private String email;
	private Date created;
	
	private String state;

	public VerificationCode_onyu() {
		super();
	}	
	
	public VerificationCode_onyu(String code, String email) {
		this.code = code;
		this.email = email;
		this.created = new Date();
	}
	
	public VerificationCode_onyu(Long id, String code, String email) {
		this.id = id;
		this.code = code;
		this.email = email;
		this.created = new Date();
	}

}
