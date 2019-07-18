package com.baeldung.um.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .requestMatchers().antMatchers("/api/users/**")
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET,"/api/users/**").access("#oauth2.hasScope('read')")
        .antMatchers(HttpMethod.POST,"/api/users/**").access("#oauth2.hasScope('write')")
        .antMatchers(HttpMethod.DELETE,"/api/users/**").access("#oauth2.hasScope('write')");
    }// @formatter:on

}