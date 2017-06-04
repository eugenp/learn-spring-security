package com.baeldung.um.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.um.web.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
