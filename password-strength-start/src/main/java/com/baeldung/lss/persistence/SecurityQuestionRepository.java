package com.baeldung.lss.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.model.SecurityQuestion;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {

    SecurityQuestion findByQuestionDefinitionIdAndUserIdAndAnswer(Long questionDefinitionId, Long userId, String answer);

    SecurityQuestion findByUserId(Long userId);

}