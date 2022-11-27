package com.baeldung.lsso.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectResourceSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http.authorizeHttpRequests(authorize -> authorize
	              .antMatchers(HttpMethod.GET, "/api/projects/**")
	                .hasAuthority("SCOPE_read")
	              .antMatchers(HttpMethod.POST, "/api/projects")
	                .hasAuthority("SCOPE_write")
	              .anyRequest()
	                .authenticated())
              .oauth2ResourceServer()
                .jwt();
        return http.build();
    }//@formatter:on

}