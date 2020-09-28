package com.baeldung.lss.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.Role;
import com.baeldung.lss.web.model.User;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public MyUserDetailsService() {
        super();
    }

    // API

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        try {
            final User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("No user found with email: " + email);
            }

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true, true, true, true, getAuthorities(user.getRoles()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    // UTIL

    public final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        return roles.stream()
            .flatMap(role -> role.getPrivileges()
                .stream())
            .map(p -> new SimpleGrantedAuthority(p.getName()))
            .collect(Collectors.toList());
    }
}
