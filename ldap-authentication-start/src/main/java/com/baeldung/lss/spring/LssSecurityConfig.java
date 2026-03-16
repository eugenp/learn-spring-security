package com.baeldung.lss.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    public LssSecurityConfig() {
        super();
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception { // @formatter:off
        http
        .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/user").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/user/*").hasRole("ADMIN"))

        .formLogin((form) -> form
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/user", true))

        .logout((logout) -> logout
                .permitAll().logoutUrl("/logout"))

        .csrf((csrf) -> csrf.disable());
        return http.build();
    } // @formatter:on

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
