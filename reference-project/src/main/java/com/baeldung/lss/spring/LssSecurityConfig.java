package com.baeldung.lss.spring;

import javax.sql.DataSource;

import com.baeldung.lss.security.CustomMethodSecurityExpressionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.baeldung.lss.security.CustomAuthenticationProvider;
import com.baeldung.lss.security.CustomWebAuthenticationDetailsSource;
import com.baeldung.lss.security.LssLoggingFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class LssSecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LssLoggingFilter lssLoggingFilter;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Value("${google.auth.enabled}")
    private boolean isGoogleAuthEnabled;

    @Autowired
    @Qualifier(value = "daoAuthenticationProvider")
    private AuthenticationProvider daoAuthenticationProvider;

    @Autowired
    @Qualifier(value = "runAsAuthenticationProvider")
    public AuthenticationProvider runAsAuthenticationProvider;

    public LssSecurityConfig() {
        super();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off
        if (isGoogleAuthEnabled) {
            auth.authenticationProvider(customAuthenticationProvider);
        } else {
            auth.authenticationProvider(daoAuthenticationProvider);
        }
        auth.authenticationProvider(runAsAuthenticationProvider);

    } // @formatter:on

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http
                .addFilterBefore(lssLoggingFilter, AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/signup",
                        "/user/register",
                        "/registrationConfirm*",
                        "/badUser*",
                        "/forgotPassword*",
                        "/user/resetPassword*",
                        "/user/changePassword*",
                        "/user/savePassword*",
                        "/code*",
                        "/isUsing2FA*",
                        "/js/**").permitAll()
                .requestMatchers("/secured").hasRole("USER")//access("hasRole('USER')")
                .anyRequest().authenticated()

                .and()
                .formLogin().
                loginPage("/login").permitAll().
                loginProcessingUrl("/doLogin")
                .authenticationDetailsSource(authenticationDetailsSource)

                .and()
                .rememberMe()
                .key("lssAppKey")
                .tokenValiditySeconds(604800) // 1 week = 604800
                .tokenRepository(persistentTokenRepository())

                .and()
                .logout().permitAll().logoutUrl("/logout")

                .and()
                .sessionManagement().maximumSessions(1)
                .sessionRegistry(sessionRegistry()).and().sessionFixation().none()

                .and()
                .csrf().disable();
        return http.build();
    } // @formatter:on

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Bean("methodSecurityExpressionProvider")
    public CustomMethodSecurityExpressionProvider createMyAuthorizer() {
        return new CustomMethodSecurityExpressionProvider();
    }
}