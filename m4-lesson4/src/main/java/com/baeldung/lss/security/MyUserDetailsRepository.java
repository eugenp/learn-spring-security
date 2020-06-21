package com.baeldung.lss.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsRepository {

    private final Map<String, MyUserDetails> users;

    public MyUserDetailsRepository() {
        users = new HashMap<String, MyUserDetails>();
        users.put("user", new MyUserDetails("user", passwordEncoder().encode("pass"), "USER", "FirstOrganization"));
        users.put("john", new MyUserDetails("john", passwordEncoder().encode("123"), "USER", "SecondOrganization"));
        users.put("tom", new MyUserDetails("tom", passwordEncoder().encode("111"), "ADMIN", "SecondOrganization"));
    }

    public MyUserDetails findByUsername(String username) {
        return users.get(username);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
