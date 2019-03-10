package com.baeldung.um.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class UmSecurityConfig extends WebSecurityConfigurerAdapter {

    public UmSecurityConfig() {
        super();
    }

    //

    @Autowired
    public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception {// @formatter:off
        auth.inMemoryAuthentication()
            .withUser("john@test.com")
            .password("{noop}123")
            .roles("USER");
    }// @formatter:on

    @Override
    protected void configure(final HttpSecurity http) throws Exception { // @formatter:off
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and().formLogin().permitAll()
            .and().csrf().disable()
            ;
    } // @formatter:on

}
