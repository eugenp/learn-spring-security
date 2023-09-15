package com.baeldung.lss.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.security.CustomWebAuthenticationDetailsSource;
import com.baeldung.lss.web.model.User;
import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;

@Configuration
@ComponentScan({ "com.baeldung.lss.security" })
@EnableWebSecurity
public class LssSecurityConfig {

    @Value("${twilio.sid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

    @Autowired
    private AuthenticationProvider authProvider;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public LssSecurityConfig(PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {// @formatter:off
        http
                .authorizeHttpRequests()
                .requestMatchers("/signup", "/user/register").permitAll()
                .anyRequest().hasRole("USER")
                .and()
                .formLogin().
                loginPage("/login").permitAll().
                loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/user")
                .authenticationDetailsSource(authenticationDetailsSource)
                .and()
                .logout().permitAll().logoutUrl("/logout")
                .and()
                .csrf().disable();
        return http.build();
    } // @formatter:on

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class BasicSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {// @formatter:off
            http
                .authorizeHttpRequests()
                .requestMatchers("/code*").permitAll()
                .anyRequest()
                .hasRole("TEMP_USER")
                .and()
                .httpBasic();
            return http.build();
        }
    }

    @PostConstruct
    private void init() {
        Twilio.init(accountSid, authToken);
        String encodedPassword = this.passwordEncoder.encode("pass");
        final User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(encodedPassword);
        user.setPasswordConfirmation(encodedPassword);
        userRepository.save(user);
    }

}
