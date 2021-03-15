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
 * - Resource Server
 * - Authorization Server
 * - Client Application Server
 */
public class Oauth2ClientLiveTest {

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";
    private static final String CLIENT_BASE_URL = "http://localhost:8082";
    private static final String AUTH_SERVER_BASE_URL = "http://localhost:8083";
    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";
    private static final String RESOURCE_URL = CLIENT_BASE_URL + "/lsso-client/projects";
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
        String authSessionId = response.getCookie("JSESSIONID");
        String kcPostAuthenticationUrl = "/login";

        // open login form
        response = RestAssured.given()
            .cookie("JSESSIONID", authSessionId)
            .get(AUTH_SERVER_BASE_URL + kcPostAuthenticationUrl);

        String csrf = response.asString()
            .split("value=\"")[1].split("\"")[0];

        // do login
        response = RestAssured.given()
            .cookie("JSESSIONID", authSessionId)
            .formParams("username", USERNAME, "password", PASSWORD, "_csrf", csrf)
            .post(AUTH_SERVER_BASE_URL + kcPostAuthenticationUrl);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());
        authSessionId = response.getCookie("JSESSIONID");

        String location = URLDecoder.decode(response.getHeader(HttpHeaders.LOCATION), "UTF-8");

        // redirect to client url
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", authSessionId)
            .get(location);

        // extract authorization code
        location = response.getHeader(HttpHeaders.LOCATION);
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
        response = RestAssured.given()
            .cookie("JSESSIONID", newClientSessionId)
            .get(RESOURCE_URL);
        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());
        assertThat(response.asString()).contains("Projects : View all");
        System.out.println(response.asString());
    }

}