package org.edupoll.service;

import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.CodeException;
import org.edupoll.model.dto.request.CreateUserRequest;
import org.edupoll.model.dto.request.ValidateUserRequest;
import org.edupoll.model.entity.User;
import org.edupoll.model.entity.VerificationCode_onyu;
import org.edupoll.repository.UserRepository;
import org.edupoll.repository.VerificationCodeRepository_onyu;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService_onyu {

	private final UserRepository userRepository;

	private final VerificationCodeRepository_onyu verificationCodeRepository;

	@Transactional
	public void registerNewUser(CreateUserRequest dto) throws ExistUserEmailException, CodeException {

		VerificationCode_onyu result = verificationCodeRepository.findByEmail(dto.getEmail()).orElse(null);

//		User found = userRepository.findByEmail(dto.getEmail());
		if (userRepository.existsByEmail(dto.getEmail())) {
			if (result.getState() == null || result.getState().equals("N")) {
				throw new ExistUserEmailException();
			} else if(result.getState().equals("Y")){
				User one = new User();
				one.setEmail(dto.getEmail());
				one.setName(dto.getName());
				one.setPassword(dto.getPassword());
				userRepository.save(one);
			}
		}

	}

	@Transactional
	public void validateUser(ValidateUserRequest req) throws NotExistUserException, InvalidPasswordException {
		User found = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new NotExistUserException());

//		if(found == null) {
//			throw new NotExistUserException();
//		}

		boolean isSame = found.getPassword().equals(req.getPassword());
		if (!isSame) {
			throw new InvalidPasswordException();
		}
		// .............
	}

}
