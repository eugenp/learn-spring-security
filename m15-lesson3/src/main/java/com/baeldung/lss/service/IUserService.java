package com.baeldung.lss.service;

import com.baeldung.lss.validation.EmailExistsException;
import com.baeldung.lss.web.model.User;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

    User updateExistingUser(User user) throws EmailExistsException;

}
