package com.baeldung.lss.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.web.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    @Override
    void delete(Role role);

}
