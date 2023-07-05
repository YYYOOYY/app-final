package org.edupoll.repository;

import java.util.Optional;

import org.edupoll.model.entity.VerificationCode_onyu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository_onyu extends JpaRepository<VerificationCode_onyu, Long> {

	Optional<VerificationCode_onyu> findByEmail(String email);

}
