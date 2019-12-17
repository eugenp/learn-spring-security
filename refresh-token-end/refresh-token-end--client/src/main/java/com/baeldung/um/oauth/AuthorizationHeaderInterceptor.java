package com.baeldung.um.oauth;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class AuthorizationHeaderInterceptor implements ClientHttpRequestInterceptor {

    private OAuth2AuthorizedClientService clientService;

    public AuthorizationHeaderInterceptor(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
        String accessToken = getAccessToken();
        if (accessToken != null) {
            request.getHeaders()
                .add("Authorization", "Bearer " + accessToken);
        }
        return execution.execute(request, bytes);
    }

    private String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (authentication == null || !authentication.getClass()
            .isAssignableFrom(OAuth2AuthenticationToken.class)) {
            return null;
        }
        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = auth.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, auth.getName());
        OAuth2AccessToken accessToken = client.getAccessToken();
        if (accessToken.getExpiresAt()
            .isBefore(Instant.now())) {
            System.out.println("Refreshing .... ");
            accessToken = refreshAccessToken(client, authentication);
            OAuth2AuthorizedClient newClient = new OAuth2AuthorizedClient(client.getClientRegistration(), authentication.getName(), accessToken, client.getRefreshToken());
            clientService.saveAuthorizedClient(newClient, authentication);
        }
        return accessToken.getTokenValue();
    }

    private OAuth2AccessToken refreshAccessToken(OAuth2AuthorizedClient client, Authentication authentication) {
        ClientRegistration clientRegistration = client.getClientRegistration();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientRegistration.getClientId(), clientRegistration.getClientSecret());
        MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
        formParameters.add("grant_type", "refresh_token");
        formParameters.add("refresh_token", client.getRefreshToken()
            .getTokenValue());
        HttpEntity entity = new HttpEntity(formParameters, headers);
        String tokenUri = clientRegistration.getProviderDetails()
            .getTokenUri();
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        OAuth2AccessTokenResponse response = restTemplate.postForObject(tokenUri, entity, OAuth2AccessTokenResponse.class);
        return response.getAccessToken();
    }
}
