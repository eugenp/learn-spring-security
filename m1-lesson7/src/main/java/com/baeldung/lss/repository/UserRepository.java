package com.baeldung.lss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);
}
