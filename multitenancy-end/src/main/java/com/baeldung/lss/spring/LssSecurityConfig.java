package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.baeldung.lss.security.TenantAuthProvider;
import com.baeldung.lss.security.TenantAuthenticationDetailsSource;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    @Autowired
    private TenantAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private TenantAuthProvider tenantAuthProvider;

    public LssSecurityConfig() {
        super();
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {// @formatter:off
        auth.authenticationProvider(tenantAuthProvider);
    } // @formatter:on

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/signup",
                        "/badUser*",
                        "/js/**").permitAll()
                .anyRequest().authenticated())

        .formLogin((form) -> form
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/doLogin")
                .authenticationDetailsSource(authenticationDetailsSource))

        .logout((logout) -> logout
                .permitAll().logoutUrl("/logout"))

        .csrf((csrf) -> csrf.disable());
        return http.build();
    } // @formatter:on

}
