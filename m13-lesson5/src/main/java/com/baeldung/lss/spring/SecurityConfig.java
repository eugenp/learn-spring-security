package com.baeldung.lss.spring;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(2)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception { //@formatter:off
        http.authorizeRequests()
            .antMatchers("/","/saml/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginPage("/")
            .and()
            .logout().logoutSuccessUrl("/")
            .and()
            .csrf().disable();
    }
  //@formatter:on

}