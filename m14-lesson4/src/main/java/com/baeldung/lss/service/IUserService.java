package com.baeldung.lss.service;

import com.baeldung.lss.validation.EmailExistsException;
import com.baeldung.lss.web.model.User;
import com.yubico.client.v2.exceptions.YubicoValidationFailure;
import com.yubico.client.v2.exceptions.YubicoVerificationException;

public interface IUserService {

    User registerNewUser(User user, String otp) throws EmailExistsException, YubicoVerificationException, YubicoValidationFailure;

}
