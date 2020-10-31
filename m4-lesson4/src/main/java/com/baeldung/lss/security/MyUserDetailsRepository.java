package com.baeldung.lss.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsRepository {

    private final Map<String, MyUserDetails> users;

    public MyUserDetailsRepository() {
        users = new HashMap<String, MyUserDetails>();
        users.put("user", new MyUserDetails("user", "pass", "USER", "FirstOrganization"));
        users.put("john", new MyUserDetails("john", "123", "USER", "SecondOrganization"));
        users.put("tom", new MyUserDetails("tom", "111", "ADMIN", "SecondOrganization"));
    }

    public MyUserDetails findByUsername(String username) {
        return users.get(username);
    }
}
