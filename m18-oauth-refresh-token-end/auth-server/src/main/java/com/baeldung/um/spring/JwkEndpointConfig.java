package com.baeldung.um.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;

@Order(1)
@Import(AuthorizationServerEndpointsConfiguration.class)
@Configuration
public class JwkEndpointConfig extends AuthorizationServerSecurityConfiguration {
    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        super.configure(http);
        http.requestMatchers()
            .mvcMatchers("/endpoint/jwks.json")
            .and()
            .authorizeRequests()
            .mvcMatchers("/endpoint/jwks.json")
            .permitAll();
    }// @formatter:on
}