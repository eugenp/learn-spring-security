package com.baeldung.lss.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

@Controller
public class VerificationCodeController {

    @Value("${twilio.sender}")
    private String senderNumber;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TwilioRestClient twilioRestClient;

    //

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void sendCode(Authentication auth) throws TwilioRestException {
        final User user = userRepository.findByEmail(auth.getName());
        if (user == null) {
            return;
        }

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        final String code = new Totp(user.getSecret()).now();
        params.add(new BasicNameValuePair("Body", "The verification code is " + code));
        params.add(new BasicNameValuePair("To", user.getPhone()));
        params.add(new BasicNameValuePair("From", senderNumber));
        System.out.println(params);

        final MessageFactory messageFactory = twilioRestClient.getAccount()
            .getMessageFactory();
        final Message message = messageFactory.create(params);
        System.out.println(message.getSid());
    }

}
