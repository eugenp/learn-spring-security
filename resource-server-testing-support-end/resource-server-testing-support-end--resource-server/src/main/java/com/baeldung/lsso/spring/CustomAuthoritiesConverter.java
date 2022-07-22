package com.baeldung.lsso.spring;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;

public class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);

        String sub = jwt.getSubject();
        if (sub.equalsIgnoreCase("admin")) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_write"));
        }

        return authorities;
    }
}
