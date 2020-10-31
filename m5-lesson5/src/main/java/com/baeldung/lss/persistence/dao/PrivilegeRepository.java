package com.baeldung.lss.persistence.dao;

import com.baeldung.lss.persistence.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    public Privilege findByName(String name);

}
