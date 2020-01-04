package com.baeldung.lsso.web.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    @GetMapping("/user/info")
    public Map<String, Object> getUserInfo() {
        return Collections.singletonMap("user_name", null);
    }
}