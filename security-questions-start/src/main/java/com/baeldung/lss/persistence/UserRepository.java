package com.baeldung.lss.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
