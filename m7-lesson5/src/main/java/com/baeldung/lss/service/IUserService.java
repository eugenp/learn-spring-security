package com.baeldung.lss.service;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.User;
import com.baeldung.lss.validation.EmailExistsException;

public interface IUserService {

    // read

    User findUserByEmail(String email);

    // write

    User registerNewUser(User user) throws EmailExistsException;

    void createPasswordResetTokenForUser(User user, String token);

    PasswordResetToken getPasswordResetToken(String token);

    void changeUserPassword(User user, String password);

}
