package com.baeldung.um.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
            clients.inMemory()
                   .withClient("lssClient")
                   .secret("lssSecret")
                   .authorizedGrantTypes("client_credentials")
                   .scopes("read","write")
                   .and()
                   .withClient("lssReadOnly")
                   .secret("lssReadSecret")
                   .authorizedGrantTypes("client_credentials")
                   .scopes("read")
                   .and()
                   .withClient("lssWriteOnly")
                   .secret("lssWriteSecret")
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