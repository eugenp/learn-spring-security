package com.baeldung.um.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //		

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
            clients.inMemory()
                   .withClient("lssClient")
                   .secret("lssSecret")
                   .authorizedGrantTypes("authorization_code", "refresh_token")
                   .scopes("read","write");
            // @formatter:on
    }
    
    @Override 
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception { 
        oauthServer.checkTokenAccess("permitAll()"); 
    }
        

}
