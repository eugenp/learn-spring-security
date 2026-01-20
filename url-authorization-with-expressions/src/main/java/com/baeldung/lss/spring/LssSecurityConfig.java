package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    private PasswordEncoder passwordEncoder;

    public LssSecurityConfig(PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off 
        auth.
            inMemoryAuthentication().passwordEncoder(passwordEncoder)
            .withUser("user").password(passwordEncoder.encode("pass")).roles("USER").and()
            .withUser("admin").password(passwordEncoder.encode("pass")).roles("ADMIN")
            ;
    } // @formatter:on

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeHttpRequests((authorize) -> authorize
                //.requestMatchers("/secured").access(new WebExpressionAuthorizationManager("hasRole('USER')"))
                //.requestMatchers("/secured").access(new WebExpressionAuthorizationManager("hasIpAddress('192.168.1.0/24')"))
                //.requestMatchers("/secured").access(new WebExpressionAuthorizationManager("hasIpAddress('::1')"))
                //.requestMatchers("/secured").access(new WebExpressionAuthorizationManager("isAnonymous()"))
                //.requestMatchers("/secured").access(new WebExpressionAuthorizationManager("request.method == 'GET'"))
                //.requestMatchers("/secured").access(new WebExpressionAuthorizationManager("not hasIpAddress('::1')"))
                //.requestMatchers("/secured")
                //    .access(new WebExpressionAuthorizationManager( "hasRole('ADMIN') and principal.username == 'user'" ))
                .anyRequest().authenticated())

        .formLogin((form) -> form
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/doLogin"))

        .logout((logout) -> logout
                .permitAll().logoutUrl("/logout"))

        .csrf((csrf) -> csrf.disable());
        return http.build();
    }

}
