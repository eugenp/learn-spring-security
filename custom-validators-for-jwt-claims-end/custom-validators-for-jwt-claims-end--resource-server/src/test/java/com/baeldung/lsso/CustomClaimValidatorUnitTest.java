package com.baeldung.lsso;

import com.baeldung.lsso.spring.CustomClaimValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class CustomClaimValidatorUnitTest {

    private final CustomClaimValidator customClaimValidator = new CustomClaimValidator();

    @Test
    public void givenJwt_withValidUser_thenOK() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .header("typ", "JWT")
                .claim("preferred_username", "john@test.com")
                .build();

        OAuth2TokenValidatorResult result = customClaimValidator.validate(jwt);
        Assertions.assertFalse(result.hasErrors());
    }

    @Test
    public void givenJwt_withInvalidUser_thenFailed() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .header("typ", "JWT")
                .claim("preferred_username", "john@email.com")
                .build();

        OAuth2TokenValidatorResult result = customClaimValidator.validate(jwt);
        Assertions.assertTrue(result.hasErrors());

        OAuth2Error error = result.getErrors().iterator().next();
        Assertions.assertEquals(OAuth2ErrorCodes.ACCESS_DENIED, error.getErrorCode());
    }

    @Test
    public void givenJwt_withoutPreferredUsername_thenFailed() {
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .header("typ", "JWT")
            .claim("alternative_username", "john@test.com")
            .build();

        OAuth2TokenValidatorResult result = customClaimValidator.validate(jwt);
        Assertions.assertTrue(result.hasErrors());

        OAuth2Error error = result.getErrors().iterator().next();
        Assertions.assertEquals(OAuth2ErrorCodes.ACCESS_DENIED, error.getErrorCode());
    }
}
