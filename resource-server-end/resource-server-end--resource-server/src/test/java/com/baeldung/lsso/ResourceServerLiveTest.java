package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.baeldung.lsso.web.dto.ProjectDto;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * This Live Test requires:
 * - the Authorization Server to be running
 * - the Resource Server to be running
 *
 */
public class ResourceServerLiveTest {

    private final String redirectUrl = "http://localhost:8082/lsso-client/login/oauth2/code/custom";
    private final String authorizeUrlPattern = "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=%s&redirect_uri=" + redirectUrl;
    private final String tokenUrl = "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token";
    private final String resourceUrl = "http://localhost:8081/lsso-resource-server/api/projects";
    private final String userInfoResourceUrl = "http://localhost:8081/lsso-resource-server/user/info";

    @SuppressWarnings("unchecked")
    @Test
    public void givenUserWithReadScope_whenGetProjectResource_thenSuccess() {
        String accessToken = obtainAccessToken("read");
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(resourceUrl);
        System.out.println(response.asString());
        assertThat(response.as(List.class)).hasSizeGreaterThan(0);
    }

    @Test
    public void givenUserWithOtherScope_whenGetProjectResource_thenForbidden() {
        String accessToken = obtainAccessToken("other");
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(resourceUrl);
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void givenUserWithReadScope_whenGetUserInformationResource_thenSuccess() {
        String accessToken = obtainAccessToken("read");
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(userInfoResourceUrl);
        System.out.println(response.asString());
        assertThat(response.as(Map.class)).containsEntry("user_name", "john@test.com");
    }

    @Test
    public void givenUserWithReadScope_whenPostNewProjectResource_thenForbidden() {
        String accessToken = obtainAccessToken("read");
        System.out.println("ACCESS TOKEN: " + accessToken);
        ProjectDto newProject = new ProjectDto(null, "newProject", LocalDate.now());

        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .body(newProject)
            .post(resourceUrl);
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserWithWriteScope_whenPostNewProjectResource_thenCreated() {
        String accessToken = obtainAccessToken("read write");
        System.out.println("ACCESS TOKEN: " + accessToken);
        ProjectDto newProject = new ProjectDto(null, "newProject", LocalDate.now());

        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .body(newProject)
            .log()
            .all()
            .post(resourceUrl);
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private String obtainAccessToken(String scopes) {
        // obtain authentication url with custom codes
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(authorizeUrlPattern, scopes));
        String authSessionId = response.getCookie("AUTH_SESSION_ID");
        String kcPostAuthenticationUrl = response.asString()
            .split("action=\"")[1].split("\"")[0].replace("&amp;", "&");

        // obtain authentication code and state
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("AUTH_SESSION_ID", authSessionId)
            .formParams("username", "john@test.com", "password", "123", "credentialId", "")
            .post(kcPostAuthenticationUrl);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        // extract authorization code
        String location = response.getHeader(HttpHeaders.LOCATION);
        String code = location.split("code=")[1].split("&")[0];

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssoClient");
        params.put("redirect_uri", redirectUrl);
        params.put("client_secret", "lssoSecret");
        response = RestAssured.given()
            .formParams(params)
            .post(tokenUrl);
        return response.jsonPath()
            .getString("access_token");
    }

}
