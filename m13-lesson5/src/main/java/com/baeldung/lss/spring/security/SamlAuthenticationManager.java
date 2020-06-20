package com.baeldung.lss.spring.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SamlAuthentication;

public class SamlAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SamlAuthentication auth = (SamlAuthentication) authentication;
        String username = auth.getAssertion().getFirstAttribute("UserID").getValues().get(0).toString();
        return new UsernamePasswordAuthenticationToken(username,null, authentication.getAuthorities());
    }

}
