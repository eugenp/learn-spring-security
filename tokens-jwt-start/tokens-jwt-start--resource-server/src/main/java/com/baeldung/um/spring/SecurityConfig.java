package com.baeldung.um.spring;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET,"/user/**","/api/foos/**").hasAuthority("SCOPE_read")
            .antMatchers(HttpMethod.POST,"/api/foos/**").hasAuthority("SCOPE_write")            
            .anyRequest().authenticated();
    }// @formatter:on
}