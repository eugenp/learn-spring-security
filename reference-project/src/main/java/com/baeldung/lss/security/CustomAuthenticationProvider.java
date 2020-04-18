package com.baeldung.lss.security;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.Role;
import com.baeldung.lss.web.model.User;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        final String username = auth.getName();
        final String password = auth.getCredentials()
                .toString();
        final String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
        final User user = userRepository.findByEmail(username);

        if ((user == null) || !encoder.matches(password, user.getPassword()) || user.getEnabled() == false) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (user.getSecret() != null) { //skip validation for default users where google auth is not set.
            final Totp totp = new Totp(user.getSecret());
            try {
                if (!totp.verify(verificationCode)) {
                    throw new BadCredentialsException("Invalid verification code");
                }
            } catch (final Exception e) {
                throw new BadCredentialsException("Invalid verification code");
            }
        }

        Collection<? extends GrantedAuthority> authorities = getAuthorities(user.getRoles());

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(username, password, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}