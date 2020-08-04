package com.baeldung.lsso.spring;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class ResourceSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http.cors(withDefaults())
            .authorizeRequests()
              .antMatchers(HttpMethod.GET, "/api/projects/**")
                .hasAuthority("SCOPE_read")
              .antMatchers(HttpMethod.POST, "/api/projects")
                .hasAuthority("SCOPE_write")
              .anyRequest()
                .authenticated()
            .and()
              .oauth2ResourceServer()
                .jwt();
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