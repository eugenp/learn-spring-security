package com.baeldung.lss.spring;

import javax.annotation.PostConstruct;

import com.baeldung.lss.security.LssLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserRepository;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@EnableWebSecurity
@EnableAsync
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LssLoggingFilter lssLoggingFilter;

    public LssSecurityConfig() {
        super();
    }

    //

    @PostConstruct
    private void saveTestUser() {
        final User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("pass");
        userRepository.save(user);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off
        auth.userDetailsService(userDetailsService);
    } // @formatter:on

    @Override
    protected void configure(HttpSecurity http) throws Exception { // @formatter:off
        http
        .addFilterBefore(lssLoggingFilter, AnonymousAuthenticationFilter.class)
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
