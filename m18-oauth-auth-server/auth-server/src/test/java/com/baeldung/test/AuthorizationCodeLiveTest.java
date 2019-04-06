package com.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Needs authorization server to be running
 */
public class AuthorizationCodeLiveTest {

    String authServerport = "8083";
    String redirectUrl = "http://www.example.com/";
    String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssClient&redirect_uri=" + redirectUrl;
    String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() {
        Response response = RestAssured.given().formParams("username", "user", "password", "pass").post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        String cookieValue = response.getCookie("JSESSIONID");
        String code = obtainAuthorizationCode(cookieValue);
        String accessToken = obtainAccessToken(code);
        assertNotNull(accessToken);
    }

    private String obtainAuthorizationCode(String cookieValue) {
        RestAssured.given().cookie("JSESSIONID", cookieValue).get(authorizeUrl);
        Response response = RestAssured.given().cookie("JSESSIONID", cookieValue).post(authorizeUrl);
        String location = response.getHeader(HttpHeaders.LOCATION);

        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        assertTrue(location.contains(redirectUrl));
        return location.substring(location.indexOf("code=") + 5);
    }

    private String obtainAccessToken(String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssClient");
        params.put("redirect_uri", redirectUrl);
        Response response = RestAssured.given()// @formatter:off
            .auth()
            .basic("lssClient", "lssSecret")
            .formParams(params)
            .post(tokenUrl);// @formatter:on

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        return response.jsonPath().getString("access_token");
    }

}