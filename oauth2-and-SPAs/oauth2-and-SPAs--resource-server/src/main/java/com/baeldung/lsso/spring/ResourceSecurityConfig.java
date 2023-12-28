package com.baeldung.lsso.spring;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class ResourceSecurityConfig {

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {// @formatter:off
        http.cors(withDefaults())
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(mvc.pattern(HttpMethod.GET, "/api/projects/**")).hasAuthority("SCOPE_read")
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/projects")).hasAuthority("SCOPE_write")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
        return http.build();
    }//@formatter:on

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(singletonList("http://localhost:8082"));
        configuration.setAllowedMethods(asList("GET", "POST"));
        configuration.setAllowedHeaders(asList(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}