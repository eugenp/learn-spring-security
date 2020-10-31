package com.baeldung.lss.security;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;

@Service
@Transactional
public class LssUserDetailsService implements UserDetailsService {

    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true, true, true, true, getAuthorities(ROLE_USER));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

}