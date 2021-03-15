package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.baeldung.lsso.web.dto.ProjectDto;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TokenIntegrationTest {
    private static final String AUTH_SERVICE_BASE_URL = "http://localhost:8083/auth/realms/baeldung";
    private static final String REDIRECT_URL = "http://localhost:8082/lsso-client/login/oauth2/code/custom";
    private static final String AUTH_SERVICE_AUTHORIZE_URL = AUTH_SERVICE_BASE_URL + "/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=read write&redirect_uri=" + REDIRECT_URL;
    private static final String CONNECT_TOKEN = AUTH_SERVICE_BASE_URL + "/protocol/openid-connect/token";
    private static final String SERVER_API_PROJECTS = "http://localhost:8081/lsso-resource-server/api/projects";

    @Test
    public void givenAuthorizationCodeGrant_whenUseToken_thenSuccess() {
        Response response = getTokenInformation();

        String accessToken = response.jsonPath()
            .getString("access_token");

        assertThat(accessToken).isNotBlank();

        response = RestAssured.given()
            .auth()
            .oauth2(accessToken)
            .get(SERVER_API_PROJECTS);

        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());

        List<ProjectDto> projects = response.getBody()
            .as(List.class);
        assertThat(3).isEqualTo(projects.size());
    }

    private Response authenticateInAuthorizationServer(Response response) {
        String authSessionId = response.getCookie("AUTH_SESSION_ID");
        String kcPostAuthenticationUrl = response.asString()
            .split("action=\"")[1].split("\"")[0].replace("&amp;", "&");

        response = RestAssured.given()
            .cookie("AUTH_SESSION_ID", authSessionId)
            .formParams("username", "john@test.com", "password", "123")
            .post(kcPostAuthenticationUrl);

        return response;
    }

    private Response getTokenInformation() {
        Response response = RestAssured.get(AUTH_SERVICE_AUTHORIZE_URL);

        response = authenticateInAuthorizationServer(response);

        String location = response.getHeader(HttpHeaders.LOCATION);
        String code = location.split("code=")[1].split("&")[0];

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssoClient");
        params.put("redirect_uri", REDIRECT_URL);
        params.put("client_secret", "lssoSecret");

        response = RestAssured.given()
            .formParams(params)
            .post(CONNECT_TOKEN);
        return response;
    }

    @Test
    public void givenAuthorizationCodeGrant_whenUseRefreshedToken_thenSuccess() {
        Response response = getTokenInformation();

        String refreshToken = response.jsonPath()
            .getString("refresh_token");

        assertThat(refreshToken).isNotBlank();

        final Map<String, String> paramsRefresh = new HashMap<>();
        paramsRefresh.put("grant_type", "refresh_token");
        paramsRefresh.put("client_id", "lssoClient");
        paramsRefresh.put("refresh_token", refreshToken);
        paramsRefresh.put("client_secret", "lssoSecret");
        Response refreshResponse = RestAssured.given()
            .formParams(paramsRefresh)
            .post(CONNECT_TOKEN);

        String refreshedToken = refreshResponse.jsonPath()
            .getString("access_token");
        assertThat(refreshedToken).isNotBlank();

        response = RestAssured.given()
            .auth()
            .oauth2(refreshedToken)
            .get(SERVER_API_PROJECTS);

        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());

        List<ProjectDto> projects = response.getBody()
            .as(List.class);
        assertThat(3).isEqualTo(projects.size());
    }

    @Test
    public void givenClientCredentialsGrant_whenUseToken_thenSuccess() {
        final Map<String, String> params = new HashMap<>();
        params.put("scope", "read write");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "lssoClient");
        params.put("client_secret", "lssoSecret");

        Response response = RestAssured.given()
            .formParams(params)
            .post(CONNECT_TOKEN);

        String accessToken = response.jsonPath()
            .getString("access_token");

        assertThat(accessToken).isNotBlank();

        response = RestAssured.given()
            .auth()
            .oauth2(accessToken)
            .get(SERVER_API_PROJECTS);

        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());

        List<ProjectDto> projects = response.getBody()
            .as(List.class);
        assertThat(3).isEqualTo(projects.size());
    }
}