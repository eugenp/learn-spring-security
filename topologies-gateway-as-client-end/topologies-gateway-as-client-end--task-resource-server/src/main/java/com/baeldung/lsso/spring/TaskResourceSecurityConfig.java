package com.baeldung.lsso.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TaskResourceSecurityConfig {
    


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http.authorizeHttpRequests(authorize -> authorize
            .antMatchers(HttpMethod.GET, "/api/tasks/**")
              .hasAuthority("SCOPE_read")
            .anyRequest()
              .authenticated())
          .oauth2ResourceServer()
            .jwt();
        return http.build();
    }//@formatter:on

}
