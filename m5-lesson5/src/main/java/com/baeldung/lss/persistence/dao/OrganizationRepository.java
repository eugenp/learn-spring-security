package com.baeldung.lss.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.persistence.model.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    public Organization findByName(String name);

}
