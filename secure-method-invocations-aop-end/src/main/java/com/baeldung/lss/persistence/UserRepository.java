package com.baeldung.lss.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.web.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
