package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Needs the following to be running: 
 * - Resource Server
 * - Second Authorization Server (Tenant2)
 * - Client Application Server
 */
public class Oauth2ClientLiveTest {

    private final String redirectUrl = "http://localhost:8082/lsso-client/login/oauth2/code/custom";
    private final String resourceUrl = "http://localhost:8082/lsso-client/projects";
    private final String tenant2AuthorizationUrl = "http://localhost:8082/lsso-client/oauth2/authorization/tenant2";

    @Test
    public void givenAuthorizationCodeGrant_whenLoginUsingOauth_thenSuccess() throws UnsupportedEncodingException {

        // Start authorization process with tenant2
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(tenant2AuthorizationUrl);

        // Then proceed to calls below and extract state
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
        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("username", "john-tenant2@test.com");
        loginDetails.put("password", "123");
        loginDetails.put("credentialId", "");
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("AUTH_SESSION_ID", authSessionId)
            .formParams(loginDetails)
            .post(kcPostAuthenticationUrl);

        // extract authorization code
        String location = response.getHeader(HttpHeaders.LOCATION);
        String code = location.split("code=")[1].split("&")[0];

        // mimic oauth2login
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", clientSessionId)
            .get(redirectUrl + "?code=" + code + "&state=" + state);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("JSESSIONID");
        response = RestAssured.given()
            .cookie("JSESSIONID", newClientSessionId)
            .get(resourceUrl);
        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());
        assertThat(response.asString()).contains("Projects : View all");
        System.out.println(response.asString());
    }

}
