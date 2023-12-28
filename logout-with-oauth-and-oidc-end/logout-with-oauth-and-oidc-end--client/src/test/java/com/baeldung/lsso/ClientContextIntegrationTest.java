package com.baeldung.lsso;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest(classes = { LssoClientApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClientContextIntegrationTest {

    private final static Pair<String, String> ISSUER_URI_PROP = Pair.of("spring.security.oauth2.client.provider.custom.issuer-uri",
        "http://localhost:{PORT}/auth/realms/baeldung");

    @DynamicPropertySource
    static void buildServerUri(DynamicPropertyRegistry registry) {
        registry.add(ISSUER_URI_PROP.getKey(), () -> ISSUER_URI_PROP.getValue()
            .replace("{PORT}", String.valueOf(authServer.getPort())));
    }

    private static MockWebServer authServer;

    @BeforeAll
    public static void setUp() throws Exception {
        authServer = new MockWebServer();
        authServer.start();
        authServer.enqueue(new MockResponse().setBody(mockMetadataDiscovery())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    @AfterAll
    public static void tearDown() throws Exception {
        authServer.shutdown();
    }

    @Test
    public void whenLoadApplication_thenSuccess() {

    }

    private static String mockMetadataDiscovery() {
        //@// @formatter:off
        String metaDataDiscoveryResponsebody = "{"
            + "   \"issuer\":\"http://localhost:PORT/auth/realms/baeldung\","
            + "   \"authorization_endpoint\":\"http://localhost:PORT/auth/realms/baeldung/protocol/openid-connect/auth\","
            + "   \"token_endpoint\":\"http://localhost:PORT/auth/realms/baeldung/protocol/openid-connect/token\","
            + "   \"token_introspection_endpoint\":\"http://localhost:PORT/auth/realms/baeldung/protocol/openid-connect/token/introspect\","
            + "   \"userinfo_endpoint\":\"http://localhost:PORT/auth/realms/baeldung/protocol/openid-connect/userinfo\","
            + "   \"end_session_endpoint\":\"http://localhost:PORT/auth/realms/baeldung/protocol/openid-connect/logout\","
            + "   \"jwks_uri\":\"http://localhost:PORT/auth/realms/baeldung/protocol/openid-connect/certs\","
            + "   \"check_session_iframe\":\"http://localhost:PORT/auth/realms/baeldung/protocol/openid-connect/login-status-iframe.html\","
            + "   \"subject_types_supported\":["
            + "      \"public\","
            + "      \"pairwise\""
            + "   ]"
            + "}";
        // @formatter:on
        return metaDataDiscoveryResponsebody.replaceAll("PORT", String.valueOf(authServer.getPort()));
    }

}