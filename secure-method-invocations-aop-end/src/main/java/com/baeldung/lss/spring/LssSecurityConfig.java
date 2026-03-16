package com.baeldung.lss.spring;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.baeldung.lss.security.MyUserDetailsService;

@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan({ "com.baeldung.lss.security" })
@Configuration
public class LssSecurityConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    public LssSecurityConfig(PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeHttpRequests((authorize) -> authorize
                .anyRequest().authenticated())

        .formLogin((form) -> form
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/doLogin"))

        .logout((logout) -> logout
                .permitAll().logoutUrl("/logout"))

        .csrf((csrf) -> csrf.disable());
        return http.build();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static Advisor methodSecurityPointcut() {
        JdkRegexpMethodPointcut pattern = new JdkRegexpMethodPointcut();
        pattern.setPattern("com.baeldung.lss.web.controller.UserController.createForm*");
        return new AuthorizationManagerBeforeMethodInterceptor(pattern, AuthorityAuthorizationManager.hasRole("ADMIN"));
    }
}
