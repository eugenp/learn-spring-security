package com.baeldung.lss.spring;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    public LssSecurityConfig() {
        super();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
        .authorizeRequests()
                .antMatchers("/user").access("hasAnyRole('ADMIN','USER')")
                .antMatchers("/user/*").access("hasRole('ADMIN')")
        .and()
        .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin")

        .and()
        .logout().permitAll().logoutUrl("/logout")
        
        .and()
        .csrf().disable()
        ;
        // @formatter:on
    }

}
