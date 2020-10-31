package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.baeldung.lss.security.TenantAuthProvider;
import com.baeldung.lss.security.TenantAuthenticationDetailsSource;

@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TenantAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private TenantAuthProvider tenantAuthProvider;

    public LssSecurityConfig() {
        super();
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {// @formatter:off
        auth.authenticationProvider(tenantAuthProvider);
    } // @formatter:on

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeRequests()
                .antMatchers("/badUser*",
                        "/js/**").permitAll()
                .anyRequest().authenticated()

        .and()
        .formLogin()
            .loginPage("/login").permitAll()
            .loginProcessingUrl("/doLogin")
            .authenticationDetailsSource(authenticationDetailsSource)

        .and()
        .logout().permitAll().logoutUrl("/logout")

        .and()
        .csrf().disable()
        ;
    } // @formatter:on

}
