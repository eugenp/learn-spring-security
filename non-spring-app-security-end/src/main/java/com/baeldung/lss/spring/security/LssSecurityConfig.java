package com.baeldung.lss.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off 
        /*auth.
            inMemoryAuthentication().
            withUser("test@test.com").password("pass").            
            roles("USER");*/        
        auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
        
    } // @formatter:on

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // @formatter:off
        http
        .authorizeHttpRequests((authorize) -> authorize
                .anyRequest().authenticated())

        .httpBasic(Customizer.withDefaults());

        return http.build();
    } // @formatter:on

    @Bean
    public LssUserDetailsService lssUserDetailsService() {
        return new LssUserDetailsService();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}