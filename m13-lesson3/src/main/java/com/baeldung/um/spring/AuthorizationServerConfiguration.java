package com.baeldung.um.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
            clients.inMemory()
                   .withClient("lssClient")
                   .secret(passwordEncoder.encode("lssSecret"))
                   .authorizedGrantTypes("client_credentials")
                   .scopes("read","write")
                   .and()
                   .withClient("lssReadOnly")
                   .secret(passwordEncoder.encode("lssReadSecret"))
                   .authorizedGrantTypes("client_credentials")
                   .scopes("read")
                   .and()
                   .withClient("lssWriteOnly")
                   .secret(passwordEncoder.encode("lssWriteSecret"))
                   .authorizedGrantTypes("client_credentials")
                   .scopes("write")
                   ;
        } // @formatter:on

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore());
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

}