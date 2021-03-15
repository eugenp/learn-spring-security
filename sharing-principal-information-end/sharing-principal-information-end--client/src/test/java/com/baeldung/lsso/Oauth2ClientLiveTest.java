package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Needs the following to be running: 
 * - Project Resource Server
 * - Task Resource Server
 * - Gateway
 * - Authorization Server
 * - Client Application Server
 */
public class Oauth2ClientLiveTest {

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String CLIENT_BASE_URL = "http://localhost:8082";
    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";
    private static final String PROJECTS_RESOURCE_URL = CLIENT_BASE_URL + "/lsso-client/projects";
    private static final String ADD_PROJECT_URL = CLIENT_BASE_URL + "/lsso-client/addproject";
    private static final String TASKS_RESOURCE_URL = CLIENT_BASE_URL + "/lsso-client/tasks";
    private static final String CLIENT_AUTHORIZATION_URI = CLIENT_BASE_URL + "/lsso-client/oauth2/authorization/custom";

    @Test
    public void givenAuthorizationCodeGrant_whenLoginUsingOauth_thenSuccess() throws UnsupportedEncodingException {
        // invoke authorization request on client to obtain state and session-id
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(CLIENT_AUTHORIZATION_URI);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());
        String fullAuthorizeUrl = response.getHeader(HttpHeaders.LOCATION)
            .replace("%20", " ");
        assertThat(fullAuthorizeUrl).contains("state");

        // extract state from redirect uri
        String state = URLDecoder.decode(fullAuthorizeUrl.split("state=")[1].split("&")[0], "UTF-8");
        String clientSessionId = response.getCookie("JSESSIONID");

        // obtain authentication url with custom codes
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(fullAuthorizeUrl);
        String authSessionId = response.getCookie("AUTH_SESSION_ID");
        String kcPostAuthenticationUrl = response.asString()
            .split("action=\"")[1].split("\"")[0].replace("&amp;", "&");

        // obtain authentication code and state
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("AUTH_SESSION_ID", authSessionId)
            .formParams("username", USERNAME, "password", PASSWORD, "credentialId", "")
            .post(kcPostAuthenticationUrl);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        // extract authorization code
        String location = response.getHeader(HttpHeaders.LOCATION);
        String code = location.split("code=")[1].split("&")[0];

        // mimic oauth2login
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", clientSessionId)
            .get(REDIRECT_URL + "?code=" + code + "&state=" + state);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("JSESSIONID");

        // check projects page
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", newClientSessionId)
            .get(PROJECTS_RESOURCE_URL);
        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());
        assertThat(response.asString()).contains("Projects : View all");

        // check add project page and post
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", newClientSessionId)
            .get(ADD_PROJECT_URL);
        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());
        String responseHtml = response.asString();
        assertThat(responseHtml).contains("_csrf");
        String csrfToken = responseHtml.split("_csrf\" value=\"")[1].split("\"")[0];

        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", newClientSessionId)
            .formParam("name", "newProjectName")
            .formParam("_csrf", csrfToken)
            .post(PROJECTS_RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getHeader(HttpHeaders.LOCATION)).contains("lsso-client/projects");

        // check tasks page with valid query param
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", newClientSessionId)
            .get(TASKS_RESOURCE_URL + "?projectId=1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.asString()).contains("Tasks : View all");

        // check tasks page with invalid query param
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", newClientSessionId)
            .get(TASKS_RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
