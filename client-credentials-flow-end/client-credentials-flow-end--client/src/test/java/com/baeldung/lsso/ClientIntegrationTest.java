package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import ch.qos.logback.classic.spi.ILoggingEvent;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientIntegrationTest {
    private final static Pair<String, String> RESOURCE_SERVER_PROP = Pair.of("resourceserver.api.project.url", "http://localhost:{PORT}/lsso-resource-server/api/projects/");
    private final static Pair<String, String> AUTH_SERVER_PROP = Pair.of("spring.security.oauth2.client.provider.customClientCredentials.token-uri", "http://localhost:{PORT}/auth/realms/baeldung/protocol/openid-connect/token");

    @Value("${resourceserver.api.project.url:http://localhost:8081/lsso-resource-server/api/projects/}")
    private String projectsEndpoint;

    @Value("${spring.security.oauth2.client.provider.customClientCredentials.token-uri}")
    private String configuredTokenUri;

    @Value("${spring.security.oauth2.client.registration.customClientCredentials.scope}")
    private String configuredScope;

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + "eyJzdWIiOiJ0ZXN0LWNsaWVudCIsIm5hbWUiOiJiYWVsZHVuZyBjbGllbnQiLCJzY29wZSI6IndyaXRlIHJlYWQiLCJpYXQiOjE1MTYyMzkwMjJ9." + "wZnm_kcu2lZVB3s3OUbz7AKWvLRyiPVlLP-asZlOvt0";

    private static MockWebServer authServer;

    private static MockWebServer resourceServer;

    @DynamicPropertySource
    static void buildServerUri(DynamicPropertyRegistry registry) {
        registry.add(RESOURCE_SERVER_PROP.getKey(), () -> RESOURCE_SERVER_PROP.getValue()
            .replace("{PORT}", String.valueOf(resourceServer.getPort())));
        registry.add(AUTH_SERVER_PROP.getKey(), () -> AUTH_SERVER_PROP.getValue()
            .replace("{PORT}", String.valueOf(authServer.getPort())));
    }

    @BeforeAll
    public static void setUp() throws Exception {
        LoggerListAppender.clearEventList();

        authServer = new MockWebServer();
        resourceServer = new MockWebServer();

        String mockedAccessToken = "{" + "  \"access_token\": \"" + ACCESS_TOKEN + "\"," + "  \"token_type\": \"bearer\"," + "  \"expires_in\": 3600" + "}";

        authServer.enqueue(new MockResponse().setBody(mockedAccessToken)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String mockedResources = "[{\"id\":1,\"name\":\"Project 1\",\"dateCreated\":\"2019-06-13\"},{\"id\":2,\"name\":\"Project 2\",\"dateCreated\":\"2019-06-14\"},{\"id\":3,\"name\":\"Project 3\",\"dateCreated\":\"2019-06-15\"}]";

        resourceServer.enqueue(new MockResponse().setBody(mockedResources)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        authServer.start();
        resourceServer.start();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        authServer.shutdown();
        resourceServer.shutdown();
    }

    @Test
    public void givenAuthServerAndResourceServer_whenRequestResourceWithClientCredentials_thenProcessExecutesOk() throws Exception {
        // ensure the token request is correct
        RecordedRequest tokenRequest = authServer.takeRequest();
        assertThat(tokenRequest.getMethod()).isEqualTo(HttpMethod.POST.name());
        // ensure the tokenUri being requested is correct
        String tokenEndpointPath = new URI(configuredTokenUri).getPath();
        assertThat(tokenRequest.getPath()).isEqualTo(tokenEndpointPath);

        // ensure the token request contains the correct body
        String requestBody = URLDecoder.decode(tokenRequest.getBody()
            .readUtf8(), StandardCharsets.UTF_8.name());
        Map<String, String> mappedBody = Arrays.stream(requestBody.split("&"))
            .collect(Collectors.toMap(param -> param.split("=")[0], param -> param.split("=")[1]));
        assertThat(mappedBody).containsEntry("grant_type", "client_credentials");
        assertThat(mappedBody).containsEntry("scope", configuredScope.replace(",", ""));

        // validate initial resource request after client gets authentication
        RecordedRequest authenticatedResourceRequest = resourceServer.takeRequest();
        assertThat(authenticatedResourceRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        String projectsPath = new URI(projectsEndpoint).getPath();
        assertThat(authenticatedResourceRequest.getPath()).isEqualTo(projectsPath);
        assertThat(authenticatedResourceRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + ACCESS_TOKEN);

        Thread.sleep(1000); // wait for scheduled to log after receiving responses

        assertThat(LoggerListAppender.getEvents()).haveAtLeastOne(eventContains("Projects in the repository:"));
    }

    private Condition<ILoggingEvent> eventContains(String substring) {
        return new Condition<ILoggingEvent>(entry -> (substring == null || (entry.getFormattedMessage() != null && entry.getFormattedMessage()
            .contains(substring))), String.format("entry with message '%s'", substring));
    }
}
