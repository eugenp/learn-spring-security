package com.baeldung.lss.service;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomSamlUserDetailsService implements SAMLUserDetailsService {

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        final String username = credential.getNameID().getValue();
        return new User(username, randomAlphabetic(8), true, true, true, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
