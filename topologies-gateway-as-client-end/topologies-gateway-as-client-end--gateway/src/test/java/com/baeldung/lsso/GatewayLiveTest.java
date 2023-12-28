package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This Live Test requires:
 * - the Authorization Server to be running
 * - the Project Resource Server to be running
 * - the Task Resource Server to be running
 * - the Gateway to be running
 */
public class GatewayLiveTest {

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";
    private static final String CLIENT_BASE_URL = "http://localhost:8084";
    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/login/oauth2/code/custom";
    private static final String RESOURCE_URL = CLIENT_BASE_URL + "/projects";
    private static final String CLIENT_AUTHORIZATION_URI = CLIENT_BASE_URL + "/oauth2/authorization/custom";

    @Test
    public void givenAuthorizationCodeGrant_whenAccessProjectsUsingCookies_thenOk() {
        Response response = getAuthenticatedClientSessionResponse();

        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("SESSION");
        response = RestAssured.given()
            .cookie("SESSION", newClientSessionId)
            .get(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(List.class)).hasSizeGreaterThan(0);
        System.out.println(response.asString());
    }

    private static Response getAuthenticatedClientSessionResponse() {
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(CLIENT_AUTHORIZATION_URI);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());
        String fullAuthorizeUrl = response.getHeader(HttpHeaders.LOCATION)
            .replace("%20", " ");
        assertThat(fullAuthorizeUrl).contains("state");

        // extract state from redirect uri
        String state = URLDecoder.decode(fullAuthorizeUrl.split("state=")[1].split("&")[0], StandardCharsets.UTF_8);
        String clientSessionId = response.getCookie("SESSION");

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
            .cookie("SESSION", clientSessionId)
            .get(REDIRECT_URL + "?code=" + code + "&state=" + state);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());
        return response;
    }

    @Test
    public void givenAuthorizationCodeGrant_whenAccessProjectsUsingCookiesAndIncorrectCors_thenForbidden() {
        Response response = getAuthenticatedClientSessionResponse();

        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("SESSION");
        response = RestAssured.given()
            .header(HttpHeaders.ORIGIN, "http://incorrect-origin")
            .cookie("SESSION", newClientSessionId)
            .get(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenAuthorizationCodeGrant_whenAccessProjectsUsingCookiesAndCorrectCorsOrigin_thenOk() {
        Response response = getAuthenticatedClientSessionResponse();

        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("SESSION");
        response = RestAssured.given()
            .header(HttpHeaders.ORIGIN, "http://localhost:8082")
            .cookie("SESSION", newClientSessionId)
            .get(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void givenCorsSetup_whenRequestOptions_thenCorsHeadersRetrieved() {
        Response response = RestAssured.given()
            .header(HttpHeaders.ORIGIN, "http://localhost:8082")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,x-xsrf-token")
            .options(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("http://localhost:8082");
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)).isEqualTo("GET,POST");
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("content-type, x-xsrf-token");
    }

    @Test
    public void givenAuthorizationCodeGrant_whenCreateUsingCookiesButWithoutCsrfToken_thenForbidden() {
        Response response = getAuthenticatedClientSessionResponse();

        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("SESSION");
        response = RestAssured.given()
            .body("{\"name\": \"test\"}")
            .cookie("SESSION", newClientSessionId)
            .post(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getBody()
            .asString()).isEqualTo("An expected CSRF token cannot be found");
    }

    @Test
    public void givenAuthorizationCodeGrant_whenCreateUsingCookiesButWithCsrfToken_thenCreated() {
        Response response = getAuthenticatedClientSessionResponse();

        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("SESSION");
        String csrfToken = response.getCookie("XSRF-TOKEN");
        response = RestAssured.given()
            .body("{\"name\": \"test\"}")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-XSRF-TOKEN", csrfToken)
            .cookie("SESSION", newClientSessionId)
            .cookie("XSRF-TOKEN", csrfToken)
            .post(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

}