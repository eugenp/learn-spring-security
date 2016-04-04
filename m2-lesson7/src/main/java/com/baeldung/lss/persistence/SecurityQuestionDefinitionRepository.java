package com.baeldung.lss.persistence;

import com.baeldung.lss.model.SecurityQuestionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}