package com.baeldung.lsso.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

@Configuration
public class TaskResourceSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AbstractPreAuthenticatedProcessingFilter preAuthFilter() throws Exception {
        RequestHeaderAuthenticationFilter preAuthFilter = new RequestHeaderAuthenticationFilter();
        preAuthFilter.setPrincipalRequestHeader("BAEL-username");
        preAuthFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        TaskResourceCustomAuthenticationDetailsSource authDetailsSource = new TaskResourceCustomAuthenticationDetailsSource();
        preAuthFilter.setAuthenticationDetailsSource(authDetailsSource);
        return preAuthFilter;
    }

    @Bean
    public AuthenticationProvider preAuthAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new PreAuthenticatedGrantedAuthoritiesUserDetailsService());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http.addFilterAt(preAuthFilter(), AbstractPreAuthenticatedProcessingFilter.class)
            .csrf().disable()
            .authorizeHttpRequests(authorize -> authorize
	              .antMatchers(HttpMethod.GET, "/api/tasks/**")
	                .hasAuthority("SCOPE_read")
	              .antMatchers(HttpMethod.POST, "/api/tasks")
	                .hasAuthority("SCOPE_write")
	              .anyRequest()
	                .authenticated())
            .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }//@formatter:on

}
