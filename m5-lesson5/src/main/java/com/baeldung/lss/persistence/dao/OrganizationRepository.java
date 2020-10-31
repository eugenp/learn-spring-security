package com.baeldung.lss.persistence.dao;

import com.baeldung.lss.persistence.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    public Organization findByName(String name);

    public Optional<Organization> findById(Long id);
}
