package org.edupoll.model.dto.request;

import lombok.Data;

@Data
public class ModifyPasswordRequest {
	String beforePassword;
	String afterPassword;
}
