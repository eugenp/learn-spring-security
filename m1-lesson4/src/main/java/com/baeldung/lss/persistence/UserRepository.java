package com.baeldung.lss.persistence;

import com.baeldung.lss.web.model.User;

public interface UserRepository {

    Iterable<User> findAll();

    User save(User user);

    User findUser(Long id);

    void deleteUser(Long id);

}
