package com.baeldung.lsso.spring;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class CustomClaimValidator implements OAuth2TokenValidator<Jwt> {

    private static final String ALLOWED_DOMAIN = "@test.com";
    private static final String CUSTOM_CLAIM_NAME = "preferred_username";

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String preferred_username = jwt.getClaimAsString(CUSTOM_CLAIM_NAME);
        if (preferred_username != null && preferred_username.toLowerCase()
            .endsWith(ALLOWED_DOMAIN)) {
            return OAuth2TokenValidatorResult.success();
        } else {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED, "Only @test.com users are allowed access", null);
            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}