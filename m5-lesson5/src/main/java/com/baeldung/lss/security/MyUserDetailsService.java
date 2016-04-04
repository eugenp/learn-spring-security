package com.baeldung.lss.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.baeldung.lss.persistence.dao.CustomUserDetailsRepository;
import com.baeldung.lss.persistence.model.CustomUserDetails;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomUserDetailsRepository userRepository;

    public MyUserDetailsService() {
        super();
    }

    // API

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final CustomUserDetails user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
