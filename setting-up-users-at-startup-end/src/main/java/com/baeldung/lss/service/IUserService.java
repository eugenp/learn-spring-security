package com.baeldung.lss.service;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.User;
import com.baeldung.lss.validation.EmailExistsException;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

    User updateExistingUser(User user) throws EmailExistsException;

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    PasswordResetToken getPasswordResetToken(String token);

    void changeUserPassword(User user, String password);

}
