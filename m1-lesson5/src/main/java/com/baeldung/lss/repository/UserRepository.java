package com.baeldung.lss.repository;

import org.springframework.stereotype.Repository;

import com.baeldung.lss.model.User;

@Repository
public interface UserRepository {

    Iterable<User> findAll();

    User save(User user);

    User findUser(Long id);

    void deleteUser(Long id);

}
