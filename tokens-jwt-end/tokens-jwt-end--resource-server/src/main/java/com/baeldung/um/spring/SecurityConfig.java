package com.baeldung.um.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET,"/user/**","/api/foos/**").hasAuthority("SCOPE_read")
            .antMatchers(HttpMethod.POST,"/api/foos/**").hasAuthority("SCOPE_write")
            .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer().jwt()
            .jwkSetUri("http://localhost:8083/um-webapp-auth-server/endpoint/jwks.json");
    }// @formatter:on
}