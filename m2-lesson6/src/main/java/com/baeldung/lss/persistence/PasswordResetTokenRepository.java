package com.baeldung.lss.persistence;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUserId(Long userId);

}
