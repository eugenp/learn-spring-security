package com.baeldung.lss.web.controller;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Controller
public class VerificationCodeController {

    @Autowired
    private TwilioRestClient twilioRestClient;

    @Value("${twilio.sender}")
    private String senderNumber;

    @Autowired
    private UserRepository userRepository;

    //

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void sendCode(@RequestParam("username") String username) throws TwilioRestException {
        final User user = userRepository.findByEmail(username);
        if (user == null) {
            return;
        }

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        final String code = new Totp(user.getSecret()).now();
        params.add(new BasicNameValuePair("Body", "The verification code is " + code));
        params.add(new BasicNameValuePair("To", user.getPhone()));
        params.add(new BasicNameValuePair("From", senderNumber));
        System.out.println(params);

        final MessageFactory messageFactory = twilioRestClient.getAccount().getMessageFactory();
        final Message message = messageFactory.create(params);
        System.out.println(message.getSid());
    }

}
