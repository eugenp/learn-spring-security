package com.baeldung.lsso;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ResourceServerMockingServersIntegrationTest {

    private final static Pair<String, String> AUTH_SERVER_INTROSPECTION_PROP = Pair.of("spring.security.oauth2.resourceserver.opaque-token.introspection-uri",
        "http://localhost:{PORT}/auth/realms/baeldung/protocol/openid-connect/token/introspect");

    private final String RESOURCE_SECURED_URL = "/api/projects";

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri}")
    private String authServerIntrospectionEndpointURL;

    @Autowired
    private WebTestClient webTestClient;

    private static MockWebServer authServer;

    @DynamicPropertySource
    static void buildServerUri(DynamicPropertyRegistry registry) {
        registry.add(AUTH_SERVER_INTROSPECTION_PROP.getKey(), () -> AUTH_SERVER_INTROSPECTION_PROP.getValue()
            .replace("{PORT}", String.valueOf(authServer.getPort())));
    }

    @BeforeAll
    public static void startServers() throws Exception {
        authServer = new MockWebServer();
        authServer.start();
    }

    @BeforeEach
    public void setup() {
        webTestClient = webTestClient.mutate()
            .build();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        authServer.shutdown();
    }

    @Test
    public void givenAuthServerAndPreloadedData_whenRequestProjectsWithActiveToken_thenResponseOk() throws Exception {

        // @formatter:off
        String mockedIntrospectionResponse = "{" + 
            "  \"scope\": \"read\"," +
            "  \"client_id\": \"clientId\"," + 
            "  \"active\": true" + 
            "}";
        // @formatter:on
        authServer.enqueue(new MockResponse().setBody(mockedIntrospectionResponse)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        this.webTestClient.get()
            .uri(RESOURCE_SECURED_URL)
            .headers(headers -> headers.setBearerAuth("anyOpaqueToken"))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()")
            .isEqualTo(3)
            .jsonPath("$[0].id")
            .isEqualTo(1)
            .jsonPath("$[0].name")
            .isEqualTo("Project 1");
    }

    @Test
    public void whenNoBearerToken_thenUnauthorized() throws Exception {

        this.webTestClient.get()
            .uri(RESOURCE_SECURED_URL)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    public void whenRequestProjectsWithNotActiveToken_thenUnauthorized() throws Exception {

        // @formatter:off
        String mockedIntrospectionResponse = "{" + 
            "  \"active\": false" + 
            "}";
        // @formatter:on
        authServer.enqueue(new MockResponse().setBody(mockedIntrospectionResponse)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        this.webTestClient.get()
            .uri(RESOURCE_SECURED_URL)
            .headers(headers -> headers.setBearerAuth("anyOpaqueToken"))
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    public void givenAuthServerAndPreloadedData_whenRequestProjectsWithActiveTokenButNoScope_thenForbidden() throws Exception {

        // @formatter:off
        String mockedIntrospectionResponse = "{" +
            "  \"active\": true" + 
            "}";
        // @formatter:on
        authServer.enqueue(new MockResponse().setBody(mockedIntrospectionResponse)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        this.webTestClient.get()
            .uri(RESOURCE_SECURED_URL)
            .headers(headers -> headers.setBearerAuth("anyOpaqueToken"))
            .exchange()
            .expectStatus()
            .isForbidden();
    }
}
