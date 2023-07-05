package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class VerifyEmailRequest {
	@Email
	private String email;
}
