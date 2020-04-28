package com.baeldung.lsso.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/user/info")
    public Map<String, Object> getUserInfo() {

        return null;
    }
}
