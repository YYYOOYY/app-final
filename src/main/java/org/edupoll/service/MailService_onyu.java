package org.edupoll.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.edupoll.exception.CodeException;
import org.edupoll.model.dto.request.MailRequest;
import org.edupoll.model.dto.request.VerificationCodeRequest_onyu;
import org.edupoll.model.entity.VerificationCode_onyu;
import org.edupoll.repository.VerificationCodeRepository_onyu;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class MailService_onyu {

	private final JavaMailSender mailsender;
	
	private final VerificationCodeRepository_onyu verificationCodeRepository;
	
	public void sendTestMail(MailRequest dto) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom("dideoduf1@gmail.com");
		message.setTo(dto.getEmail());
		message.setSubject("메일테스트");
		message.setText("메일 테스트중입니다.\n불편을 드려 죄송합니다.");
		
		
		mailsender.send(message);
		
	}
	
	public void sendTestHtmlMail(MailRequest dto) throws MessagingException, UnsupportedEncodingException {
		
		String uuid = UUID.randomUUID().toString();
		
		Random random = new Random();
		int randNum = random.nextInt(1000000);
		String code = String.format("%06d", randNum);
		
		String htmlTxt = """
				<div>
				<h1>메일테스트중</h1>
				<p style="color:orange">
					HTML 메세지도 <b>전송</b> 가능하다.
				</p>
				<p>
					인증번호 : <i>#code</i>
				</p>
			</div>
			""".replaceAll("#code", code);
		
//		formatted(uuid)
		
		MimeMessage message = mailsender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(new InternetAddress("dideoduf1@gmail.com", "[인증]테스트", "UTF-8"));
		
		helper.setTo(dto.getEmail());
		helper.setSubject("메일테스트");
		helper.setText(htmlTxt, true);
		
		mailsender.send(message);
	}
	
	public void sendEmailCode(MailRequest dto) throws UnsupportedEncodingException, MessagingException, CodeException {
			
		Random random = new Random();
		int randNum = random.nextInt(1000000);
		String code = String.format("%06d", randNum);
		
		Optional<VerificationCode_onyu> result = verificationCodeRepository.findByEmail(dto.getEmail());
		
		if(result.isEmpty()) {
			verificationCodeRepository.save(new VerificationCode_onyu(code, dto.getEmail()));			
		}else if(result.get().getState().equals("Y")){
			throw new CodeException("이미 인증에 설공한 이메일입니다.");
		}else {
			verificationCodeRepository.save(new VerificationCode_onyu(result.get().getId(), code, result.get().getEmail()));
		}
		
		String htmlTxt = """
				<div>
				<h1>인증코드</h1>
				<p style="color:orange">
					화면에 나온 <b>인증코드</b>를 홈페이지에 입력해주세요
				</p>
				<p>
					인증코드 : <i>#code</i>
				</p>
			</div>
			""".replaceAll("#code", code);
		
		MimeMessage message = mailsender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(new InternetAddress("dideoduf1@gmail.com", "코드인증", "UTF-8"));
		
		helper.setTo(dto.getEmail());
		helper.setSubject("인증코드");
		helper.setText(htmlTxt, true);
		
		mailsender.send(message);
	}

	public void checkCodeHandle(VerificationCodeRequest_onyu req) throws CodeException, MessagingException, UnsupportedEncodingException {
		
		Optional<VerificationCode_onyu> result = verificationCodeRepository.findById(req.getId());
		
		if(result.isEmpty()) {
			result.get().setState("N");
			verificationCodeRepository.save(result.get());

			throw new CodeException("이메일 인증코드를 다시 받아주시길 바랍니다");
		}
		
		if(result.get().getState().equals("Y")) {
			throw new CodeException("이미 인증에 성공한 이메일입니다.");
		}
		
		if(result.get().getCode().equals("재인증필요")) {
			Random random = new Random();
			int randNum = random.nextInt(1000000);
			String code = String.format("%06d", randNum);
			
			Optional<VerificationCode_onyu> rst = verificationCodeRepository.findByEmail(req.getEmail());
			
			verificationCodeRepository.save(new VerificationCode_onyu(req.getId(), code, req.getEmail()));			

			
			String htmlTxt = """
					<div>
					<h1>인증코드</h1>
					<p style="color:orange">
						화면에 나온 <b>인증코드</b>를 홈페이지에 입력해주세요
					</p>
					<p>
						인증코드 : <i>#code</i>
					</p>
				</div>
				""".replaceAll("#code", code);
			
			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(new InternetAddress("dideoduf1@gmail.com", "코드인증", "UTF-8"));
			
			helper.setTo(req.getEmail());
			helper.setSubject("[프로젝트]인증코드");
			helper.setText(htmlTxt, true);
			
			mailsender.send(message);
			return;
		}
		
		if(!result.get().getEmail().equals(req.getEmail())) {
			result.get().setState("N");
			verificationCodeRepository.save(result.get());
			throw new CodeException("잘못된 접근입니다.");
		}
		
		if(!result.get().getCode().equals(req.getCode())) {
			result.get().setState("N");
			result.get().setCode("재인증필요");
			verificationCodeRepository.save(result.get());
			throw new CodeException("인증 실패");
		}
				
		result.get().setState("Y");
		
		verificationCodeRepository.save(result.get());
	}
	
}
