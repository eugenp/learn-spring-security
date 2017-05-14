package com.baeldung.lss.service;

import com.baeldung.lss.model.User;
import com.baeldung.lss.validation.EmailExistsException;

public interface IUserService {

    // read

    User findUserByEmail(String email);

    // write

    User registerNewUser(User user) throws EmailExistsException;

    Iterable<User> findAll();

}
