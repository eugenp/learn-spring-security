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
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestHandler;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class LssoGatewaySecurity {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {//@formatter:off
        CookieServerCsrfTokenRepository csrfRepository = CookieServerCsrfTokenRepository.withHttpOnlyFalse();
        ServerCsrfTokenRequestAttributeHandler requestHandler = new SpaCsrfTokenRequestHandler();

        return http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().authenticated())
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

    static final class SpaCsrfTokenRequestHandler extends ServerCsrfTokenRequestAttributeHandler {
        private final ServerCsrfTokenRequestHandler delegate = new XorServerCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(ServerWebExchange exchange, Mono<CsrfToken> csrfToken) {
            this.delegate.handle(exchange, csrfToken);
        }

        @Override
        public Mono<String> resolveCsrfTokenValue(ServerWebExchange exchange, CsrfToken csrfToken) {
            if (StringUtils.hasText(csrfToken.getHeaderName())) {
                return super.resolveCsrfTokenValue(exchange, csrfToken);
            }
            return this.delegate.resolveCsrfTokenValue(exchange, csrfToken);
        }

    }
}
