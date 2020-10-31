package com.baeldung.lss.spring;

import java.util.Arrays;
import java.util.Collection;

import javax.ejb.EJB;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.baeldung.lss.model.User;
import com.baeldung.lss.service.UserServiceInterface;

public class LssUserDetailsService implements UserDetailsService {

    private static final String ROLE_USER = "ROLE_USER";

    @EJB(mappedName = "java:module/userService")
    UserServiceInterface userService;

    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getEnabled(), true, true, true, getAuthorities(ROLE_USER));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

}
