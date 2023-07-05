package org.edupoll.controller;

import java.io.IOException;

import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.DeleteUserRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.response.LogonUserInfoResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.KakaoAPIService;
import org.edupoll.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.NotSupportedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user/private")
@CrossOrigin
@Slf4j
public class PrivateController {

	private final UserService userService;
	private final JWTService jwtService;
	private final KakaoAPIService kakaoAPIService;

	@GetMapping
	public ResponseEntity<?> getLogonUserHandle(Authentication authentication) throws NotExistUserException {
		log.info("authentication : {} , {} ", authentication, authentication.getPrincipal());

		String principal = (String) authentication.getPrincipal();
		UserWrapper wrapper = userService.searchUserByEmail(principal);

		LogonUserInfoResponse response = new LogonUserInfoResponse(200, wrapper);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteUserHandle(@AuthenticationPrincipal String principal, DeleteUserRequest req)
			throws NotExistUserException, InvalidPasswordException {
		if (principal.endsWith("@kakao.user")) { // 소셜로 가입한 유저 삭제하기
			// 카카오에 unlink 요청하기 (access Token)
			kakaoAPIService.sendUnlink(principal);
			// DB에서 데이터 삭제하기
			userService.deleteSpecificSocialUser(principal);

		} else { // 자체관리중인 유저 삭제하기
			// DB에서 데이터 삭제하기
			userService.deleteSpecificUser(principal, req);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	// 사용자 상태(프로필 이미지 / 이름) 업데이트 처리할 API
	// 파일업로드는 컨텐츠타입이 multipart/form-data 로 들어옴.
	// (file과 text 유형이 섞여 있음)
	@PostMapping("/info")
	public ResponseEntity<?> updateProfileHandle(@AuthenticationPrincipal String principal,
			UpdateProfileRequest request) throws IOException, NotSupportedException, NotExistUserException {

		userService.modifySpecificUser(principal, request);
		var wrapper = userService.searchUserByEmail(principal);

		var response = new LogonUserInfoResponse(200, wrapper);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
