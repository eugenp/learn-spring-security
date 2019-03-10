package com.baeldung.um.spring;

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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("john@test.com").password("{noop}123").roles("USER");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception { // @formatter:off
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and().formLogin().permitAll()
            .and().csrf().disable()
            ;
    } // @formatter:on

}
