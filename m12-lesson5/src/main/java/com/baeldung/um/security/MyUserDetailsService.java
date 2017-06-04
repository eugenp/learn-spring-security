package com.baeldung.um.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.baeldung.um.service.IUserService;
import com.baeldung.um.web.model.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Component
public final class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    public MyUserDetailsService() {
        super();
    }

    // API - public

    @Override
    public final UserDetails loadUserByUsername(final String username) {
        Preconditions.checkNotNull(username);

        final User user = userService.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username was not found: " + username);
        }

        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), Lists.newArrayList());
    }

}
