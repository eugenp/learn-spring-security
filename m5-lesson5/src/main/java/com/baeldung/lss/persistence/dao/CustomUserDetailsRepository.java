package com.baeldung.lss.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.persistence.model.CustomUserDetails;

public interface CustomUserDetailsRepository extends JpaRepository<CustomUserDetails, Long> {

    public CustomUserDetails findByUsername(String username);

}
