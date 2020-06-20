package com.baeldung.lss.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.persistence.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    public Privilege findByName(String name);

}
