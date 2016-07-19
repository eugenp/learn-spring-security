package com.baeldung.um.service;

import com.baeldung.um.validation.EmailExistsException;
import com.baeldung.um.web.model.User;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

}
