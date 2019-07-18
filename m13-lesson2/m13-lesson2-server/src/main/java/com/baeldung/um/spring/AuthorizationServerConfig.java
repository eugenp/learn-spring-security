package com.baeldung.um.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
            clients.inMemory()
                   .withClient("lssClient")
                   .secret(passwordEncoder.encode("lssSecret"))
                   .authorizedGrantTypes("authorization_code", "refresh_token")
                   .scopes("read","write")
                   .redirectUris("http://localhost:8082/um-webapp-client/user");
            // @formatter:on
    }

}
