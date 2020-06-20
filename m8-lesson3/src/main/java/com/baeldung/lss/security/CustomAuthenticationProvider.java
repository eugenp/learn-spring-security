package com.baeldung.lss.security;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = authentication.getCredentials()
            .toString();

        if (!supportsAuthentication(authentication)) {
            return null;
        }

        if (doAuthenticationAgainstThirdPartySystem()) {
            return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
        } else {
            throw new BadCredentialsException("Authentication against the third party system failed");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    //

    private boolean doAuthenticationAgainstThirdPartySystem() {
        return true;
    }

    private boolean supportsAuthentication(Authentication authentication) {
        return true;
    }

}