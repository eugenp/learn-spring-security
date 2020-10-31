package com.baeldung.lss.service;

import com.baeldung.lss.model.User;
import com.baeldung.lss.validation.EmailExistsException;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

    User updateExistingUser(User user) throws EmailExistsException;

    User findUserByEmail(String email);


    void changeUserPassword(User user, String password);

}
