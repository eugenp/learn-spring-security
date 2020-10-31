package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off 
        /*auth.
            inMemoryAuthentication().
            withUser("test@test.com").password("pass").            
            roles("USER");*/        
        auth.userDetailsService(userDetailsService);
        
    } // @formatter:on

    @Override
    protected void configure(HttpSecurity http) throws Exception { // @formatter:off
        http
        .authorizeRequests()
                .anyRequest().authenticated()
        
        .and()
        .httpBasic();
    } // @formatter:on

    @Bean
    public LssUserDetailsService userDetailsService() {
        return new LssUserDetailsService();
    }
}