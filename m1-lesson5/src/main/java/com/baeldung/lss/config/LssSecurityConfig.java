package com.baeldung.lss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.
            inMemoryAuthentication().
            withUser("user").password("{noop}pass").
            roles("USER");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	 http
         .authorizeRequests()
                 .anyRequest().authenticated()
         
         .and()
        .formLogin().
        	loginPage("/login").permitAll().
        	loginProcessingUrl("/doLogin")
        ;
    } 
    

}
