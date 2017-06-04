package com.baeldung.lss.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ComponentScan({ "com.baeldung.lss.security" })
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    public LssSecurityConfig() {
        super();
    }

    //

    @Override
    protected void configure(HttpSecurity http) throws Exception { // @formatter:off
        http.authorizeRequests()
            .anyRequest()
            .authenticated()
            
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .loginProcessingUrl("/doLogin")
            
            .and()
            .logout()
            .permitAll()
            .logoutUrl("/logout")
            
            .and()
            .csrf()
            .disable();
    }// @formatter:on

}
