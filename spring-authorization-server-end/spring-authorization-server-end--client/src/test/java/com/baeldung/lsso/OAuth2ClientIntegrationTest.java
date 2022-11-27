package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2ClientIntegrationTest {

    private final static Pair<String, String> RESOURCE_SERVER_PROP = Pair.of("resourceserver.api.project.url", "http://localhost:{PORT}/lsso-resource-server/api/projects/");

    private static MockWebServer resourceServer;

    @Value("${resourceserver.api.project.url}")
    private String projectsUrl;

    @Autowired
    private MockMvc mvc;

    @DynamicPropertySource
    static void buildServerUri(DynamicPropertyRegistry registry) {
        registry.add(RESOURCE_SERVER_PROP.getKey(), () -> RESOURCE_SERVER_PROP.getValue()
            .replace("{PORT}", String.valueOf(resourceServer.getPort())));
    }

    @BeforeAll
    public static void startServers() throws Exception {
        resourceServer = new MockWebServer();
        resourceServer.start();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        resourceServer.shutdown();
    }

    @Test
    void givenMockedUser_whenRequestResources_thenOK() throws Exception {
        String mockedResources = "[{\"id\":1,\"name\":\"Project 1\",\"dateCreated\":\"2015-06-01\"}]";

        resourceServer.enqueue(new MockResponse().setBody(mockedResources)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        mvc.perform(get("/projects").with(oidcLogin())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Project 1")));

        RecordedRequest request = resourceServer.takeRequest();
        assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)).startsWith("Bearer");
    }

}
