package com.baeldung.lsso.spring;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class CustomAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
        defaultConverter.setAuthoritiesClaimName("scope");
        defaultConverter.setAuthorityPrefix("SCOPE_");

        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);
        String username = jwt.getClaimAsString("preferred_username");
        if (username.contains("@")) {
            authorities.add(new SimpleGrantedAuthority("EMAIL_USERNAME"));
        }
        return authorities;
    }
}
