package com.baeldung.lss.web.controller;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class VerificationCodeController {

    @Value("${twilio.sender}")
    private String senderNumber;

    @Autowired
    private UserRepository userRepository;

    //

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void sendCode(Authentication auth) {
        final User user = userRepository.findByEmail(auth.getName());
        if (user == null) {
            return;
        }

        final String code = new Totp(user.getSecret()).now();
        final String messageBody = "The verification code is " + code;

        System.out.println("messageBody:" + messageBody);

        final Message message = Message.creator(new PhoneNumber(user.getPhone()), new PhoneNumber(senderNumber), messageBody).create();
        System.out.println(message.getSid());

    }

}
