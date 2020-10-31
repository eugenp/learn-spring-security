package com.baeldung.lss.spring;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.annotation.PostConstruct;

@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    public LssSecurityConfig() {
        super();
    }

    //
    private void cleanUpExistingUsers() {
        userRepository.deleteAll();
    }

    @PostConstruct
    private void saveTestUser() {
        cleanUpExistingUsers();
        final User user = new User();
        user.setEmail("test@email.com");
        user.setPassword(encoder().encode("pass"));
        userRepository.save(user);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {// @formatter:off
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(encoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider);
    } // @formatter:on

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
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

    @Bean
    public PasswordEncoder encoder() {
        return new StandardPasswordEncoder();
    }

}
