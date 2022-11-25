package com.baeldung.lsso.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceSecurityConfig {

    private final AuthServersConfig authServersConfig;

    @Autowired
    public ResourceSecurityConfig(AuthServersConfig authServersConfig) {
        this.authServersConfig = authServersConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
//        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver = new JwtIssuerAuthenticationManagerResolver(authServersConfig.getTrustedIssuerUris());
        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver = new JwtIssuerAuthenticationManagerResolver
            ((issuer) -> {
                JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(authServersConfig.getIssuersJwkSetURIs().get(issuer))
                .jwsAlgorithm(SignatureAlgorithm.RS256).build();
                AuthenticationProvider authProvider = new JwtAuthenticationProvider(jwtDecoder);
                return new ProviderManager(authProvider);
            });

        http.authorizeHttpRequests(authorize -> authorize
	              .antMatchers(HttpMethod.GET, "/api/projects/**")
	                .hasAuthority("SCOPE_read")
	              .antMatchers(HttpMethod.POST, "/api/projects")
	                .hasAuthority("SCOPE_write")
	              .anyRequest()
	                .authenticated())
              .oauth2ResourceServer(oauth -> oauth.authenticationManagerResolver(authenticationManagerResolver));
        return http.build();
    }//@formatter:on

}