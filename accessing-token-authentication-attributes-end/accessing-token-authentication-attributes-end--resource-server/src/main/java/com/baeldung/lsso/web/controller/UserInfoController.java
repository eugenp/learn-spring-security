package com.baeldung.lsso.web.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    @GetMapping("/user/info")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return Collections.singletonMap("token", jwt);
    }

    @GetMapping("/user/info/direct")
    public Map<String, Object> getDirectUserInfo(@AuthenticationPrincipal Jwt principal) {
        String username = (String) principal.getClaims()
            .get("preferred_username");

        return Collections.singletonMap("username", username);
    }

}
