package com.baeldung.lsso.spring;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;

public class CustomAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {

    @Override
    public GrantedAuthoritiesContainer buildDetails(HttpServletRequest context) {
        Enumeration<String> headerValues = context.getHeaders("BAEL-authorities");
        Collection<GrantedAuthority> authorities = Collections.list(headerValues)
            .stream()
            .map(value -> new SimpleGrantedAuthority(value))
            .collect(Collectors.toList());
        return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, authorities);
    }

}
