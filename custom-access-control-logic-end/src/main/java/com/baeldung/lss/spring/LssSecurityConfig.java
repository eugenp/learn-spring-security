package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    private PasswordEncoder passwordEncoder;

    private AuthorizationManager<RequestAuthorizationContext> baseAccessRules;

    public LssSecurityConfig(PasswordEncoder passwordEncoder, AuthorizationManager<RequestAuthorizationContext> baseAccessRules) {
        super();
        this.passwordEncoder = passwordEncoder;
        this.baseAccessRules = baseAccessRules;
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
            .requestMatchers("/secured").access(AuthorizationManagers.allOf(
                baseAccessRules,
                AuthorityAuthorizationManager.hasRole("ADMIN")
                ))
            .anyRequest().access(baseAccessRules)
        )
        .formLogin((form) -> form
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/doLogin"))

        .logout((logout) -> logout
                .permitAll().logoutUrl("/logout"))

        .csrf((csrf) -> csrf.disable());
        return http.build();
    }
}
