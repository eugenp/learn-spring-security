package com.baeldung.lss.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UsersFromUserBuilder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean("fromUserBuilder")
    public UserDetailsService inMemoryUserDetailsManager() {
        final User.UserBuilder userBuilder = User.builder();
        final UserDetails defaultUser = userBuilder.username("userbuilder@email.com")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build();
        final UserDetails user = userBuilder.username("user@email.com")
            .password(passwordEncoder.encode("pass"))
            .roles("USER")
            .build();
        final UserDetails admin = userBuilder.username("admin@email.com")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, defaultUser, admin);
    }
}
