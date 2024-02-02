package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class Oauth2ClientIntegrationTest {

    private final static Pair<String, String> AUTH_SERVER_AUTH_URI_PROP = Pair.of("spring.security.oauth2.client.provider.spring.authorization-uri",
        "http://localhost:{PORT}/oauth2/authorize");
    private final static Pair<String, String> AUTH_SERVER_TOKEN_PROP = Pair.of("spring.security.oauth2.client.provider.spring.token-uri",
        "http://localhost:{PORT}/oauth2/token");
    private final static Pair<String, String> AUTH_SERVER_CERTS_PROP = Pair.of("spring.security.oauth2.client.provider.spring.jwk-set-uri",
        "http://localhost:{PORT}/oauth2/jwks");
    private final static Pair<String, String> RESOURCE_SERVER_PROP = Pair.of("resourceserver.api.project.url",
        "http://localhost:{PORT}/lsso-resource-server/api/projects");

    private final String CLIENT_SECURED_URL = "/projects";
    private final String REDIRECT_URI = "/login/oauth2/code/spring?state=%s&code=%s";

    @Autowired
    ObjectMapper objectMapper;

    @Value("${spring.security.oauth2.client.provider.spring.authorization-uri}")
    private String authServerAuthorizationURL;

    @Value("${spring.security.oauth2.client.registration.spring.redirect-uri}")
    private String configuredRedirectUri;

    @Value("${spring.security.oauth2.client.provider.spring.token-uri}")
    private String configuredTokenUri;

    @Value("${spring.security.oauth2.client.provider.spring.jwk-set-uri}")
    private String jwkSetURI;

    @Value("${resourceserver.api.project.url}")
    private String projectsUrl;

    @Autowired
    private WebTestClient webTestClient;

    private static MockWebServer authServer;
    private static MockWebServer resourceServer;

    @DynamicPropertySource
    static void buildServerUri(DynamicPropertyRegistry registry) {
        registry.add(RESOURCE_SERVER_PROP.getKey(), () -> RESOURCE_SERVER_PROP.getValue()
            .replace("{PORT}", String.valueOf(resourceServer.getPort())));
        registry.add(AUTH_SERVER_AUTH_URI_PROP.getKey(), () -> AUTH_SERVER_AUTH_URI_PROP.getValue()
            .replace("{PORT}", String.valueOf(authServer.getPort())));
        registry.add(AUTH_SERVER_TOKEN_PROP.getKey(), () -> AUTH_SERVER_TOKEN_PROP.getValue()
            .replace("{PORT}", String.valueOf(authServer.getPort())));
        registry.add(AUTH_SERVER_CERTS_PROP.getKey(), () -> AUTH_SERVER_CERTS_PROP.getValue()
            .replace("{PORT}", String.valueOf(authServer.getPort())));
    }

    @BeforeAll
    public static void startServers() throws Exception {
        resourceServer = new MockWebServer();
        authServer = new MockWebServer();
        authServer.start();
        resourceServer.start();
    }

    @BeforeEach
    public void setup() {
        webTestClient = webTestClient.mutate()
            .responseTimeout(Duration.ofMillis(300000))
            .build();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        authServer.shutdown();
        resourceServer.shutdown();
    }

    @Test
    void givenAuthServerAndResourceServer_whenPerformClientLoginProcess_thenProcessExecutesOk() throws Exception {
        // mimic login button action
        ExchangeResult result = this.webTestClient.get()
            .uri(CLIENT_SECURED_URL)
            .exchange()
            .expectStatus()
            .isFound()
            .expectHeader()
            .value(HttpHeaders.LOCATION, endsWith("/oauth2/authorization/spring"))
            .returnResult(Void.class);

        // redirects to 'custom' OAuth authorization endpoint
        String cookieSession = result.getResponseCookies()
            .getFirst("JSESSIONID")
            .getValue();
        String redirectTarget = result.getResponseHeaders()
            .getFirst(HttpHeaders.LOCATION);

        result = this.webTestClient.get()
            .uri(redirectTarget)
            .cookie("JSESSIONID", cookieSession)
            .exchange()
            .expectStatus()
            .isFound()
            .expectHeader()
            .value(HttpHeaders.LOCATION, startsWith(authServerAuthorizationURL))
            .returnResult(Void.class);

        // request to authorization endpoint contains state attribute
        String authorizationURL = result.getResponseHeaders()
            .getFirst(HttpHeaders.LOCATION);
        String state = URLDecoder.decode(authorizationURL.split("state=")[1].split("&")[0], StandardCharsets.UTF_8);
        String nonce = URLDecoder.decode(authorizationURL.split("nonce=")[1].split("&")[0], StandardCharsets.UTF_8);

        // Initialise a RSAKey Generator
        // To be used for signing the mock token
        RSAKey rsaJWK = new RSAKeyGenerator(2048).keyID("123")
            .generate();

        // prepare token endpoint response
        String idToken = mockIdToken(nonce, rsaJWK);
        String accessToken = "abc987";
        String mockedAccessToken = mockAccessToken(idToken, accessToken);
        authServer.enqueue(new MockResponse().setBody(mockedAccessToken)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // prepare JWKSet Endpoint response exposing public keys
        String mockJWKResponse = mockJwkSet(rsaJWK);
        authServer.enqueue(new MockResponse().setBody(mockJWKResponse)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // prepare UserInfo mocked response
        String mockedUserInfo = mockUserInfo();
        authServer.enqueue(new MockResponse().setBody(mockedUserInfo)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // send request to redirect_uri with code and state
        String code = "123";
        result = this.webTestClient.get()
            .uri(String.format(REDIRECT_URI, state, code))
            .cookie("JSESSIONID", cookieSession)
            .exchange()
            .expectStatus()
            .isFound()
            .expectHeader()
            .value(HttpHeaders.LOCATION, endsWith(CLIENT_SECURED_URL + "?continue"))
            .returnResult(Void.class);

        // assert that Access Token Endpoint was requested as expected
        RecordedRequest capturedTokenRequest = authServer.takeRequest();
        assertThat(capturedTokenRequest.getMethod()).isEqualTo(HttpMethod.POST.name());
        String tokenEndpointPath = new URI(configuredTokenUri).getPath();
        assertThat(capturedTokenRequest.getPath()).isEqualTo(tokenEndpointPath);
        String requestBody = URLDecoder.decode(capturedTokenRequest.getBody()
            .readUtf8(), StandardCharsets.UTF_8);
        Map<String, String> mappedBody = Arrays.stream(requestBody.split("&"))
            .collect(Collectors.toMap(param -> param.split("=")[0], param -> param.split("=")[1]));
        assertThat(mappedBody).containsEntry("grant_type", "authorization_code");
        assertThat(mappedBody).containsEntry("code", code);
        assertThat(mappedBody).containsEntry("redirect_uri", configuredRedirectUri);

        // assert that jwkSet endpoint was requested as expected
        RecordedRequest capturedJWKSetRequest = authServer.takeRequest();
        assertThat(capturedJWKSetRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        String jwkSetPath = new URI(jwkSetURI).getPath();
        assertThat(capturedJWKSetRequest.getPath()).isEqualTo(jwkSetPath);

        String mockedResources = "[{\"id\":1,\"name\":\"Project 1\",\"dateCreated\":\"2019-06-13\"},{\"id\":2,\"name\":\"Project 2\",\"dateCreated\":\"2019-06-14\"},{\"id\":3,\"name\":\"Project 3\",\"dateCreated\":\"2019-06-15\"}]";

        resourceServer.enqueue(new MockResponse().setBody(mockedResources)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // now we're redirected back to the /projects endpoint
        // when accessing it, Client should send Access Token as Bearer token in header
        String newCookieSession = result.getResponseCookies()
            .getFirst("JSESSIONID")
            .getValue();

        this.webTestClient.get()
            .uri(CLIENT_SECURED_URL)
            .cookie("JSESSIONID", newCookieSession)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(response -> {
                String bodyAsString = new String(response.getResponseBodyContent());
                assertThat(bodyAsString).contains("Project 1")
                    .contains("Project 2")
                    .contains("Project 3")
                    .doesNotContain("Project 4");
            });

        RecordedRequest capturedProjectRequest = resourceServer.takeRequest();
        assertThat(capturedProjectRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        String projectsPath = new URI(projectsUrl).getPath();
        assertThat(capturedProjectRequest.getPath()).isEqualTo(projectsPath);
        assertThat(capturedProjectRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + accessToken);
    }

    @Test
    public void whenUnauthorized_thenRedirect() throws Exception {
        this.webTestClient.get()
            .uri(CLIENT_SECURED_URL)
            .exchange()
            .expectStatus()
            .is3xxRedirection();
    }

    private String mockIdToken(String nonce, RSAKey rsaJWK) throws JOSEException {
        JWSSigner signer = new RSASSASigner(rsaJWK);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject("fakeSub")
            .issuer("http://localhost/mock.com")
            .audience("lssoClient")
            .issueTime(new Date())
            .expirationTime(new Date(new Date().getTime() + 600 * 1000))
            .claim("nonce", nonce)
            .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID())
            .build(), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    private String mockAccessToken(String idToken, String tokenValue) {
        //@formatter:off
        return "{" +
            "  \"access_token\": \"" + tokenValue + "\"," +
            "  \"token_type\": \"bearer\"," +
            "  \"expires_in\": 3600," +
            "  \"scope\": \"openid read write\"," +
            "  \"id_token\": \"" + idToken + "\"" +
            "}";
    }

    private String mockJwkSet(RSAKey rsaJWK) throws JsonProcessingException {
        JWKSet jwk = new JWKSet(rsaJWK);
        return objectMapper.writeValueAsString(jwk.toJSONObject());
    }

    private String mockUserInfo() {
        //@formatter:off
        return "{" +
            "  \"sub\": \"fakeSub\"" +
            "}";
    }

}