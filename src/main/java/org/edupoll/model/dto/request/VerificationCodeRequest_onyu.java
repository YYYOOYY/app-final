package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class VerificationCodeRequest_onyu {
	
	Long id;
	
	String code;
	@Email
	String email;
}
