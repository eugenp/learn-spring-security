package com.baeldung.lss.persistence;

import com.baeldung.lss.model.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.Security;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {

    SecurityQuestion findByQuestionDefinitionIdAndUserIdAndAnswer(Long questionDefinitionId, Long userId, String answer);

    SecurityQuestion findByUserId(Long userId);

}