package com.baeldung.lsso.spring;

import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.DelegatingServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class LssoGatewaySecurity {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {//@formatter:off
        CookieServerCsrfTokenRepository csrfRepository = CookieServerCsrfTokenRepository.withHttpOnlyFalse();
        ServerCsrfTokenRequestAttributeHandler requestHandler = new ServerCsrfTokenRequestAttributeHandler();

        return http.authorizeExchange()
           .anyExchange()
             .authenticated()
           .and()
             .oauth2Login(oauth2 -> oauth2.authenticationSuccessHandler(
                 new DelegatingServerAuthenticationSuccessHandler(
                     cookieCsrfHandler(csrfRepository),
                     new RedirectServerAuthenticationSuccessHandler("http://localhost:8082/lsso-client/"))))
             .csrf(csrf -> {
                 csrf.csrfTokenRepository(csrfRepository);
                 csrf.csrfTokenRequestHandler(requestHandler);
             })
           .build();
        //@formatter:on
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(GlobalCorsProperties globalCorsProperties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        globalCorsProperties.getCorsConfigurations()
            .forEach(source::registerCorsConfiguration);
        return source;
    }

    private ServerAuthenticationSuccessHandler cookieCsrfHandler(CookieServerCsrfTokenRepository csrfRepository) {
        return (exchangeFilter, authentication) -> {
            return csrfRepository.generateToken(exchangeFilter.getExchange())
                .delayUntil((token) -> csrfRepository.saveToken(exchangeFilter.getExchange(), token))
                .then();
        };
    }
}
