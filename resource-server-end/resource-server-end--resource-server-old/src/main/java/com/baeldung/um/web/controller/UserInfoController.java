package com.baeldung.um.web.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserInfoController {

    @RequestMapping(method = RequestMethod.GET, value = "/user/info")
    @ResponseBody
    public Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
        return Collections.singletonMap("user_name", auth.getName());
    }
}