package com.baeldung.lss.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class LssSecurityConfig {

    private PasswordEncoder passwordEncoder;

    public LssSecurityConfig(PasswordEncoder passwordEncoder) {
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
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        return httpSecurity
            .authorizeExchange((authorize) -> authorize
                    .pathMatchers("/user/delete/*").hasRole("ADMIN")
                    .anyExchange()
                    .authenticated())
            .httpBasic(Customizer.withDefaults())
            .csrf((csrf) -> csrf.disable())
                .build();
         // @formatter:on
    }
}
