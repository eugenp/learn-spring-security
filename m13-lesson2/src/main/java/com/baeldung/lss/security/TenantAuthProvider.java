package com.baeldung.lss.security;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class TenantAuthProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    private void after() {
        this.setUserDetailsService(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        final Principal principal = (Principal) userDetails;
        if (!principal.getTenant().equals(authentication.getDetails())) {
            throw new BadCredentialsException("Incorrect tenant information");
        }

        super.additionalAuthenticationChecks(userDetails, authentication);
    }

}
