package com.baeldung.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthorizationCodeLiveTest {
    final String resourceServerport = "8081";
    final String authServerport = "8083";
    final String redirectUrl = "http://www.example.com/";
    final String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssClient&redirect_uri=" + redirectUrl;
    final String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";
    final String resourceUrl = "http://localhost:" + resourceServerport + "/um-webapp-resource-server/api/users";

    @Test
    public void givenAccessToken_whenGetUserResource_thenSuccess() {
        String accessToken = obtainAccessToken();

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(resourceUrl);
        System.out.println(response.asString());
        assertTrue(response.as(List.class)
            .size() > 0);
    }

    private String obtainAccessToken() {
        Response response = RestAssured.given()
            .formParams("username", "john@test.com", "password", "123")
            .post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        final String cookieValue = response.getCookie("JSESSIONID");
        RestAssured.given()
            .cookie("JSESSIONID", cookieValue)
            .get(authorizeUrl);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_oauth_approval", "true");
        params.put("authorize", "Authorize");
        params.put("scope.read", "true");
        response = RestAssured.given()
            .cookie("JSESSIONID", cookieValue)
            .formParams(params)
            .post(authorizeUrl);

        final String location = response.getHeader(HttpHeaders.LOCATION);
        final String code = location.substring(location.indexOf("code=") + 5);

        // get access token
        Map<String, String> tokenParams = new HashMap<String, String>();
        tokenParams.put("grant_type", "authorization_code");
        tokenParams.put("code", code);
        tokenParams.put("client_id", "lssClient");
        tokenParams.put("redirect_uri", redirectUrl);
        Response tokenResponse = RestAssured.given()
            .auth()
            .preemptive()
            .basic("lssClient", "lssSecret")
            .queryParams(tokenParams)
            .post(tokenUrl);
        return tokenResponse.jsonPath()
            .getString("access_token");
    }

}
