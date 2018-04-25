package com.baeldung.um.service;

import com.baeldung.um.validation.EmailExistsException;
import com.baeldung.um.web.model.User;

public interface IUserService {

    // read

    User findByEmail(String email);

    // write

    User registerNewUser(User user) throws EmailExistsException;

}
