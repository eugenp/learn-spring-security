package com.baeldung.um.spring;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

/**
 * 
 * We're implementing our own {@link OpaqueTokenIntrospector} only because of a bug in the Spring Security OAuth Authorization Server's {@code /oauth/check_token} endpoint.
 * 
 * Because of this, the Introspection Endpoint retrieves the 'scopes' as a JSON array of Strings,
 * instead of a plain String value separating its values with a space as indicated in the Introspection Endpoint specs.
 * 
 * This causes that the default {@link NimbusOpaqueTokenIntrospector} introspector doesn't extract correctly the SCOPE_* authorities.
 * 
 *  @see https://github.com/spring-projects/spring-security-oauth/issues/1558
 *  @see https://tools.ietf.org/html/rfc7662#section-2.2
 *
 */
@Component
public class CustomAuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private OpaqueTokenIntrospector delegate;

    public CustomAuthoritiesOpaqueTokenIntrospector(@Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}") String introspectionUri, @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}") String clientId,
        @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}") String clientSecret) {
        delegate = new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2AuthenticatedPrincipal principal = this.delegate.introspect(token);
        return new DefaultOAuth2AuthenticatedPrincipal(principal.getName(), principal.getAttributes(), extractAuthorities(principal));
    }

    private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
        List<String> scopes = principal.getAttribute(OAuth2IntrospectionClaimNames.SCOPE);
        return scopes.stream()
            .map(scope -> "SCOPE_" + scope)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
