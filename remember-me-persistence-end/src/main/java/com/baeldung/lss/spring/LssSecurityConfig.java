package com.baeldung.lss.spring;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    public LssSecurityConfig(PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    } // @formatter:on

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/signup",
                        "/user/register",
                        "/registrationConfirm*",
                        "/badUser*",
                        "/forgotPassword*",
                        "/user/resetPassword*",
                        "/user/changePassword*",
                        "/user/savePassword*",
                        "/js/**").permitAll()
                .anyRequest().authenticated())

        .formLogin((form) -> form
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/doLogin"))

        .rememberMe((rememberMe) -> rememberMe
                .tokenRepository(persistentTokenRepository()))

        .logout((logout) -> logout
                .permitAll().logoutUrl("/logout"))

        .csrf((csrf) -> csrf.disable());
        return http.build();
    } // @formatter:on

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

}
