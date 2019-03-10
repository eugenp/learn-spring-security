package com.baeldung.um.oauth;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class AuthorizationHeaderInterceptor implements ClientHttpRequestInterceptor {

    private OAuth2AuthorizedClientService clientService;

    public AuthorizationHeaderInterceptor(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = null;
        if (authentication != null && authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = auth.getAuthorizedClientRegistrationId();
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, auth.getName());
            accessToken = client.getAccessToken().getTokenValue();
            request.getHeaders().add("Authorization", "Bearer " + accessToken);
        }
        return execution.execute(request, bytes);
    }
}
