package com.baeldung.um.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .requestMatchers().antMatchers("/api/foos/**","/user/**")
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET,"/user/**").access("#oauth2.hasScope('read')")
        .antMatchers(HttpMethod.GET,"/api/foos/**").access("#oauth2.hasScope('read')")
        .antMatchers(HttpMethod.POST,"/api/foos/**").access("#oauth2.hasScope('write')");
    }// @formatter:on

    @Bean
    public RemoteTokenServices tokenServices() {
        final RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl("http://localhost:8083/um-webapp-auth-server/oauth/check_token");
        tokenService.setClientId("lssoClient");
        tokenService.setClientSecret("lssoSecret");
        return tokenService;
    }

}