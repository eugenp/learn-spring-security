package com.baeldung.lss.persistence.dao;

import com.baeldung.lss.persistence.model.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomUserDetailsRepository extends JpaRepository<CustomUserDetails, Long> {

    public CustomUserDetails findByUsername(String username);

}
