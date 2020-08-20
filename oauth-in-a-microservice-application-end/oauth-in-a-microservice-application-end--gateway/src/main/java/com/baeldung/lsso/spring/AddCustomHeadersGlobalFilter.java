package com.baeldung.lsso.spring;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AddCustomHeadersGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
            .filter(Authentication.class::isInstance)
            .cast(Authentication.class)
            .map(authentication -> addHeaders(exchange, authentication))
            .flatMap(chain::filter);

    }

    private ServerWebExchange addHeaders(ServerWebExchange exchange, Authentication authentication) {
        String[] authorities = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toArray(String[]::new);
        exchange.getRequest()
            .mutate()
            .header("BAEL-authorities", authorities)
            .header("BAEL-username", authentication.getName());
        return exchange;
    }

}
