package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.baeldung.lss.security.CustomMethodSecurityExpressionHandler;
import com.baeldung.lss.security.CustomMethodSecurityExpressionProvider;

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
        .authorizeHttpRequests()
            .anyRequest().permitAll()
        .and()
        
        .httpBasic()
        
        .and()
        .csrf().disable();
        return http.build();
    }
    
//    @Bean("alternativeMethodSecurityExpressionHandler")
    static MethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();
        return expressionHandler;
    }
    
    @Bean("methodSecurityExpressionProvider")
    public CustomMethodSecurityExpressionProvider createMyAuthorizer() {
    	return new CustomMethodSecurityExpressionProvider();
    }
}
