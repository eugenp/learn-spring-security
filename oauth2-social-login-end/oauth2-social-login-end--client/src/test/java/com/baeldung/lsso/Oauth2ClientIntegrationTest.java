package com.baeldung.lsso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class Oauth2ClientIntegrationTest {

    public static final String JSESSIONID = "JSESSIONID";
    private final String CLIENT_BASE_URL = "/lsso-client";
    private final String CLIENT_SECURED_URL = CLIENT_BASE_URL + "/projects";
    private final String CLIENT_REDIRECT_URL = CLIENT_BASE_URL + "login/oauth2/code/github";

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() throws Exception {
        webTestClient = webTestClient.mutate()
            .responseTimeout(Duration.ofMillis(300000))
            .build();
    }

    @Test
    public void whenPerformClientLoginProcess_thenProcessRedirectsProperly() throws Exception {
        ExchangeResult result = this.webTestClient.get()
            .uri(CLIENT_SECURED_URL)
            .exchange()
            .expectStatus()
            .isFound()
            .expectHeader()
            .value(HttpHeaders.LOCATION, endsWith("/oauth2/authorization/github"))
            .returnResult(Void.class);

        String cookieSession = result.getResponseCookies()
            .getFirst(JSESSIONID)
            .getValue();
        String redirectTarget = result.getResponseHeaders()
            .getFirst(HttpHeaders.LOCATION);

        ExchangeResult resultGithub =this.webTestClient.get()
            .uri(redirectTarget)
            .cookie(JSESSIONID, cookieSession)
            .exchange()
            .expectStatus()
            .isFound()
            .expectHeader()
            .value(HttpHeaders.LOCATION, startsWith("https://github.com/login/oauth/authorize"))
            .returnResult(Void.class);

        String redirectTargetGithub = resultGithub.getResponseHeaders()
            .getFirst(HttpHeaders.LOCATION);

        assertThat(redirectTargetGithub.contains(clientId));
        assertThat(redirectTargetGithub.contains(CLIENT_REDIRECT_URL));
    }

    @Test
    public void whenUnauthorized_thenRedirect() throws Exception {
        this.webTestClient.get()
            .uri(CLIENT_SECURED_URL)
            .exchange()
            .expectStatus()
            .is3xxRedirection();
    }
}