package com.baeldung.lss.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class LssSecurityConfig {

    private PasswordEncoder passwordEncoder;

    public LssSecurityConfig(PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .passwordEncoder(passwordEncoder::encode)
            .username("user")
            .password("pass")
            .roles("USER")
            .build();
        UserDetails admin = User.builder()
            .passwordEncoder(passwordEncoder::encode)
            .username("admin")
            .password("pass")
            .roles("ADMIN")
            .build();
        return new MapReactiveUserDetailsService(user, admin);
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity httpSecurity) throws Exception { // @formatter:off
        return httpSecurity
            .authorizeExchange()
            .pathMatchers("/user/delete/*").hasRole("ADMIN")
            .anyExchange()
            .authenticated()
                .and()
            .httpBasic()
                .and()
            .csrf()
            .disable()
                .build();
    } // @formatter:on
}
