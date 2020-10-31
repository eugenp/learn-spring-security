package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    public LssSecurityConfig() {
        super();
    }
    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {// @formatter:off
//            auth.inMemoryAuthentication().
//                withUser("user").password("pass").roles("USER").and().
//                withUser("admin").password("pass").roles("ADMIN");

            auth.
                jdbcAuthentication().dataSource(dataSource)//.withDefaultSchema()
                .withUser("user").password("pass").roles("USER")
                .and().withUser("admin").password("pass").roles("ADMIN");

    }// @formatter:on

    @Override protected void configure(HttpSecurity http) throws Exception { // @formatter:off
        http
            .authorizeRequests()
            .antMatchers("/signup",
                "/user/register",
                "/registrationConfirm*",
                "/badUser*",
                "/forgotPassword*",
                "/user/resetPassword*",
                "/user/changePassword*",
                "/user/savePassword*",
                "/js/**").permitAll()
            .anyRequest().authenticated()

            .and()
            .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin")

            .and()
            .logout().permitAll().logoutUrl("/logout")

            .and()
            .csrf().disable()
        ;
    } // @formatter:on
}
