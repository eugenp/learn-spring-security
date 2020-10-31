package com.baeldung.lss.service;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.User;
import com.baeldung.lss.validation.EmailExistsException;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(final User user, final String token);

    PasswordResetToken getPasswordResetToken(final String token);

    void changeUserPassword(final User user, final String password);

}
