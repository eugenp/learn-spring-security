package com.baeldung.lsso.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class ProjectResourceSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AbstractPreAuthenticatedProcessingFilter preAuthFilter() throws Exception {
        RequestHeaderAuthenticationFilter preAuthFilter = new RequestHeaderAuthenticationFilter();
        preAuthFilter.setPrincipalRequestHeader("BAEL-username");
        preAuthFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        CustomAuthenticationDetailsSource authDetailsSource = new CustomAuthenticationDetailsSource();
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
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {// @formatter:off
        http.addFilterAt(preAuthFilter(), AbstractPreAuthenticatedProcessingFilter.class)
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(mvc.pattern(HttpMethod.GET, "/api/projects/**")).hasAuthority("SCOPE_read")
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/projects")).hasAuthority("SCOPE_write")
                .anyRequest().authenticated())
            .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }//@formatter:on

}
