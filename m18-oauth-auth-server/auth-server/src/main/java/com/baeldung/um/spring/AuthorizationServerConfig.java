package com.baeldung.um.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

//@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
        clients.inMemory()
               .withClient("lssClient")
               .secret(passwordEncoder.encode("lssSecret"))
               .authorizedGrantTypes("authorization_code")
               .scopes("read", "write")
               .redirectUris("http://www.example.com/")  
               .autoApprove(true);
        // @formatter:on
    }

}
