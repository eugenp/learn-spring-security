package com.baeldung.um.spring;

import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

//@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
        clients.inMemory()
               .withClient("lssClient")
               .secret("{noop}lssSecret")
               .authorizedGrantTypes("authorization_code")
               .scopes("read", "write")
               .redirectUris("http://www.example.com/")  
               .autoApprove(true);
        // @formatter:on
    }

}
