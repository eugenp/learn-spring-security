package com.baeldung.lss.service;

import com.baeldung.lss.model.VerificationToken;
import com.baeldung.lss.validation.EmailExistsException;
import com.baeldung.lss.model.User;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String token);

    void saveRegisteredUser(User user);
}
