package com.baeldung.lss.security;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;
import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private YubicoClient yubicoClient;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        final String username = auth.getName();
        final String password = auth.getCredentials()
                .toString();
        final String otp = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
        final User user = userRepository.findByEmail(username);

        if ((user == null) || !encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        try {
            final VerificationResponse response = yubicoClient.verify(otp);
            if (!response.isOk()) {
                throw new BadCredentialsException("Invalid Yubico key");
            }
            final String yubicoPublicId = YubicoClient.getPublicId(otp);
            if (!user.getYubicoPublicId()
                    .equals(yubicoPublicId)) {
                throw new BadCredentialsException("Invalid Yubico ID");
            }

        } catch (final Exception e) {
            throw new BadCredentialsException("Invalid Yubico key");
        }
        return new UsernamePasswordAuthenticationToken(user, password, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
