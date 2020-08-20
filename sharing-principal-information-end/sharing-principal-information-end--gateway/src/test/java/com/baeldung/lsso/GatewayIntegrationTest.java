package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import java.net.URI;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class GatewayIntegrationTest {

    private static final String BASE_GATEWAY_URL_PATH = "/lsso-gateway";
    private static final String TASK_SVC_ENDPOINT_URL = "/lsso-task-resource-server/api/tasks";
    private static final String PROJECT_SVC_ENDPOINT_URL = "/lsso-project-resource-server/api/projects";

    @Autowired
    private WebTestClient webTestClient;

    MockWebServer projectResourceServer = new MockWebServer();

    MockWebServer taskResourceServer = new MockWebServer();

    @BeforeEach
    public void setUp() throws Exception {
        projectResourceServer.start(8081);
        taskResourceServer.start(8085);
    }

    @AfterEach
    public void tearDown() throws Exception {
        projectResourceServer.shutdown();
        taskResourceServer.shutdown();
    }

    @Test
    public void givenJwt_whenRequestProjectEndpoint_thenRequestForwardedToProjectServer() throws Exception {
        String mockedProjectResources = "[{\"id\":1,\"name\":\"Project 1\",\"dateCreated\":\"2019-06-13\"},{\"id\":2,\"name\":\"Project 2\",\"dateCreated\":\"2019-06-14\"},{\"id\":3,\"name\":\"Project 3\",\"dateCreated\":\"2019-06-15\"}]";

        projectResourceServer.enqueue(new MockResponse().setBody(mockedProjectResources)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        this.webTestClient.mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "read write custom")
            .subject("customSubjectId")))
            .get()
            .uri(BASE_GATEWAY_URL_PATH + "/projects")
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

        RecordedRequest capturedProjectRequest = projectResourceServer.takeRequest();
        assertThat(capturedProjectRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        String projectsPath = new URI(PROJECT_SVC_ENDPOINT_URL).getPath();
        assertThat(capturedProjectRequest.getPath()).isEqualTo(projectsPath);
        assertThat(capturedProjectRequest.getHeaders()
            .toMultimap()).hasEntrySatisfying("BAEL-authorities", valueList -> valueList.containsAll(Arrays.asList("SCOPE_write", "SCOPE_read", "SCOPE_custom")));
        assertThat(capturedProjectRequest.getHeader("BAEL-username")).isEqualTo("customSubjectId");
    }

    @Test
    public void givenJwt_whenRequestTaskEndpoint_thenRequestForwardedToTaskServer() throws Exception {
        String mockedTaskResources = "[{\"id\":1,\"name\":\"Task 1\",\"description\":\"Description of Task 1\"}]";

        taskResourceServer.enqueue(new MockResponse().setBody(mockedTaskResources)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String tasksQueryParamsSection = "?projectId=1";

        this.webTestClient.mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "read write custom")
            .subject("customSubjectId")))
            .get()
            .uri(BASE_GATEWAY_URL_PATH + "/tasks" + tasksQueryParamsSection)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(response -> {
                String bodyAsString = new String(response.getResponseBodyContent());
                assertThat(bodyAsString).contains("Task 1")
                    .doesNotContain("Task 2");
            });

        RecordedRequest capturedTaskRequest = taskResourceServer.takeRequest();
        assertThat(capturedTaskRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        URI tasksPath = new URI(TASK_SVC_ENDPOINT_URL + tasksQueryParamsSection);
        assertThat(capturedTaskRequest.getPath()).isEqualTo(tasksPath.getPath() + "?" + tasksPath.getQuery());
        assertThat(capturedTaskRequest.getHeaders()
            .toMultimap()).hasEntrySatisfying("BAEL-authorities", valueList -> valueList.containsAll(Arrays.asList("SCOPE_write", "SCOPE_read", "SCOPE_custom")));
        assertThat(capturedTaskRequest.getHeader("BAEL-username")).isEqualTo("customSubjectId");
    }

    @Test
    public void givenJwt_whenRequestUnmappedEndpoint_then404NotFound() throws Exception {
        this.webTestClient.mutateWith(mockJwt().jwt(jwt -> jwt.claim("scope", "read write custom")
            .subject("customSubjectId")))
            .get()
            .uri(BASE_GATEWAY_URL_PATH + "/other")
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    public void givenNoJwt_whenHttpGet_thenUnauthorized() throws Exception {
        this.webTestClient.get()
            .uri(BASE_GATEWAY_URL_PATH + "projects/")
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

}
