package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class Oauth2ClientIntegrationTest {

    private static final String CLIENT_SECURED_PROJECTS_URL = "/projects";
    private static final String CLIENT_SECURED_ADD_PROJECT_URL = "/addproject";
    private static final String CLIENT_SECURED_TASKS_URL = "/tasks";
    private static final String REDIRECT_URI = "/login/oauth2/code/custom?state=%s&code=%s";

    @Value("${spring.security.oauth2.client.provider.custom.authorization-uri}")
    private String authServerAuthorizationURL;

    @Value("${spring.security.oauth2.client.registration.custom.redirect-uri}")
    private String configuredRedirectUri;

    @Value("${spring.security.oauth2.client.provider.custom.token-uri}")
    private String configuredTokenUri;

    @Value("${spring.security.oauth2.client.provider.custom.user-info-uri}")
    private String configuredUserInfoUri;

    @Value("${gateway.url}")
    private String gatewayBaseUrl;

    @Autowired
    private WebTestClient webTestClient;

    MockWebServer authServer;

    MockWebServer gatewayServer;

    @BeforeEach
    public void setUp() throws Exception {
        webTestClient = webTestClient.mutate()
            .responseTimeout(Duration.ofMillis(30000))
            .build();

        createAuthServer();

        createGatewayServer();
    }

    @AfterEach
    public void tearDown() throws Exception {
        authServer.shutdown();
        gatewayServer.shutdown();
    }

    private void createGatewayServer() throws IOException {
        gatewayServer = new MockWebServer();
        gatewayServer.start(8084);
    }

    private void createAuthServer() throws IOException {
        authServer = new MockWebServer();
        authServer.start(8083);
    }

    @Test
    public void givenAuthServerAndResourceServer_whenPerformClientLoginProcess_thenProcessExecutesOk() throws Exception {
        // mimic login button action
        ExchangeResult result = this.webTestClient.get()
            .uri(CLIENT_SECURED_PROJECTS_URL)
            .exchange()
            .expectStatus()
            .isFound()
            .expectHeader()
            .value(HttpHeaders.LOCATION, endsWith("/oauth2/authorization/custom"))
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
        String state = URLDecoder.decode(authorizationURL.split("state=")[1].split("&")[0], StandardCharsets.UTF_8.toString());

        // prepare Access Token mocked response
        String accessToken = "abc987";
        // @formatter:off
        String mockedAccessToken = "{" + 
            "  \"access_token\": \"" + accessToken + "\"," + 
            "  \"token_type\": \"bearer\"," + 
            "  \"expires_in\": 3600" + 
            "}";
        // @formatter:on
        authServer.enqueue(new MockResponse().setBody(mockedAccessToken)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // prepare UserInfo mocked response
        // @formatter:off
        String mockedUserInfo = "{" + 
            "  \"preferred_username\": \"theUsername\"" +
            "}";
        // @formatter:on
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
            .value(HttpHeaders.LOCATION, endsWith(CLIENT_SECURED_PROJECTS_URL))
            .returnResult(Void.class);

        // assert that Access Token Endpoint was requested as expected
        RecordedRequest capturedTokenRequest = authServer.takeRequest();
        assertThat(capturedTokenRequest.getMethod()).isEqualTo(HttpMethod.POST.name());
        String tokenEndpointPath = new URI(configuredTokenUri).getPath();
        assertThat(capturedTokenRequest.getPath()).isEqualTo(tokenEndpointPath);
        String requestBody = URLDecoder.decode(capturedTokenRequest.getBody()
            .readUtf8(), StandardCharsets.UTF_8.name());
        Map<String, String> mappedBody = Arrays.stream(requestBody.split("&"))
            .collect(Collectors.toMap(param -> param.split("=")[0], param -> param.split("=")[1]));
        assertThat(mappedBody).containsEntry("grant_type", "authorization_code");
        assertThat(mappedBody).containsEntry("code", code);
        assertThat(mappedBody).containsEntry("redirect_uri", configuredRedirectUri);

        // assert UserInfo request
        RecordedRequest capturedUserInfoRequest = authServer.takeRequest();
        assertThat(capturedUserInfoRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        String userInfoPath = new URI(configuredUserInfoUri).getPath();
        assertThat(capturedUserInfoRequest.getPath()).isEqualTo(userInfoPath);
        assertThat(capturedUserInfoRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + accessToken);

        String mockedProjectResources = "[{\"id\":1,\"name\":\"Project 1\",\"dateCreated\":\"2019-06-13\"},{\"id\":2,\"name\":\"Project 2\",\"dateCreated\":\"2019-06-14\"},{\"id\":3,\"name\":\"Project 3\",\"dateCreated\":\"2019-06-15\"}]";

        gatewayServer.enqueue(new MockResponse().setBody(mockedProjectResources)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // now we're redirected back to the /projects endpoint
        // when accessing it, Client should send Access Token as Bearer token in header
        String newCookieSession = result.getResponseCookies()
            .getFirst("JSESSIONID")
            .getValue();

        this.webTestClient.get()
            .uri(CLIENT_SECURED_PROJECTS_URL)
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

        RecordedRequest capturedProjectRequest = gatewayServer.takeRequest();
        assertThat(capturedProjectRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        String projectsPath = new URI(gatewayBaseUrl + "projects/").getPath();
        assertThat(capturedProjectRequest.getPath()).isEqualTo(projectsPath);
        assertThat(capturedProjectRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + accessToken);

        // we can also validate creating a new Project
        FluxExchangeResult<String> resultPage = this.webTestClient.get()
            .uri(CLIENT_SECURED_ADD_PROJECT_URL)
            .cookie("JSESSIONID", newCookieSession)
            .exchange()
            .returnResult(String.class);

        Optional<String> csrfToken = resultPage.getResponseBody()
            .toStream()
            .filter(str -> str.contains("_csrf"))
            .findFirst()
            .map(htmlSection -> htmlSection.split("_csrf\" value=\"")[1].split("\"")[0]);

        assertThat(csrfToken).isPresent();

        gatewayServer.enqueue(new MockResponse().setResponseCode(HttpStatus.CREATED.value()));

        this.webTestClient.post()
            .uri(CLIENT_SECURED_PROJECTS_URL)
            .cookie("JSESSIONID", newCookieSession)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData("name", "newProjectName")
                .with("_csrf", csrfToken.get()))
            .exchange()
            .expectStatus()
            .isFound()
            .expectHeader()
            .value(HttpHeaders.LOCATION, containsString("lsso-client/projects"));

        RecordedRequest capturedAddProjectRequest = gatewayServer.takeRequest();
        assertThat(capturedAddProjectRequest.getMethod()).isEqualTo(HttpMethod.POST.name());
        String addProjectsPath = new URI(gatewayBaseUrl + "projects/").getPath();
        assertThat(capturedAddProjectRequest.getPath()).isEqualTo(addProjectsPath);
        assertThat(capturedAddProjectRequest.getBody()
            .readUtf8()).contains("newProjectName");
        assertThat(capturedAddProjectRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + accessToken);

        // we can also validate access to the /tasks endpoint
        String mockedTaskResources = "[{\"id\":1,\"name\":\"Task 1\",\"description\":\"Description of Task 1\"}]";

        gatewayServer.enqueue(new MockResponse().setBody(mockedTaskResources)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String tasksQueryParamsSection = "?projectId=1";

        this.webTestClient.get()
            .uri(CLIENT_SECURED_TASKS_URL + tasksQueryParamsSection)
            .cookie("JSESSIONID", newCookieSession)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(response -> {
                String bodyAsString = new String(response.getResponseBodyContent());
                assertThat(bodyAsString).contains("Task 1")
                    .doesNotContain("Task 2");
            });

        RecordedRequest capturedTasksRequest = gatewayServer.takeRequest();
        assertThat(capturedTasksRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        URI tasksPath = new URI(gatewayBaseUrl + "tasks/" + tasksQueryParamsSection);
        assertThat(capturedTasksRequest.getPath()).isEqualTo(tasksPath.getPath() + "?" + tasksPath.getQuery());
        assertThat(capturedTasksRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + accessToken);

        // also checking invalid request, missing required query param
        this.webTestClient.get()
            .uri(CLIENT_SECURED_TASKS_URL)
            .cookie("JSESSIONID", newCookieSession)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void whenUnauthorized_thenRedirect() throws Exception {
        this.webTestClient.get()
            .uri(CLIENT_SECURED_PROJECTS_URL)
            .exchange()
            .expectStatus()
            .is3xxRedirection();
    }
}