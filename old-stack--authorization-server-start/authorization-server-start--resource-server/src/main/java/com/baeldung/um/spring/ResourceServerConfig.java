package com.baeldung.um.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .requestMatchers().antMatchers("/api/users/**","/users/**")
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET,"/users/**").access("#oauth2.hasScope('read')")
        .antMatchers(HttpMethod.GET,"/api/users/**").access("#oauth2.hasScope('read')")
        .antMatchers(HttpMethod.POST,"/api/users/**").access("#oauth2.hasScope('write')");
    }// @formatter:on       

    @Primary
    @Bean
    public RemoteTokenServices tokenServices() {
        final RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl("http://localhost:8083/um-webapp-auth-server/oauth/check_token");
        tokenService.setClientId("lssoClient");
        tokenService.setClientSecret("lssoSecret");
        return tokenService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
