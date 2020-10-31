package com.baeldung.lss.spring;

import com.baeldung.lss.security.CustomAuthenticationProvider;
import com.baeldung.lss.security.CustomWebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ComponentScan({ "org.baeldung.lss.security" })
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    public LssSecurityConfig() {
        super();
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeRequests()
                .antMatchers("/signup", "/user/register","/code*","/isUsing2FA*").permitAll()
                .anyRequest().authenticated()

        .and()
        .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin")
            .authenticationDetailsSource(authenticationDetailsSource)

        .and()
        .logout().permitAll().logoutUrl("/logout")

        .and()
        .csrf().disable()
        ;
    } // @formatter:on

}
