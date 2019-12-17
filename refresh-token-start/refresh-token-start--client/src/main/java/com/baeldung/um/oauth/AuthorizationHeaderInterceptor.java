package com.baeldung.um.oauth;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

public class AuthorizationHeaderInterceptor implements ClientHttpRequestInterceptor {

    private OAuth2AuthorizedClientService clientService;

    public AuthorizationHeaderInterceptor(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {

        return execution.execute(request, bytes);
    }

}
