package com.baeldung.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Needs the following to be running: 
 * - Resource Server
 * - Authorization Server
 */
public class ResourceServerLiveTest {
    final String resourceServerport = "8081";
    final String authServerport = "8083";
    final String redirectUrl = "http://localhost:8082/um-webapp-client/login/oauth2/code/custom";
    final String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssoClient&redirect_uri=" + redirectUrl;
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
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", cookieValue)
            .post(authorizeUrl);
        final String location = response.getHeader(HttpHeaders.LOCATION);
        final String code = location.substring(location.indexOf("code=") + 5);

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssoClient");
        params.put("redirect_uri", redirectUrl);
        response = RestAssured.given()
            .auth()
            .basic("lssoClient", "lssoSecret")
            .formParams(params)
            .post(tokenUrl);
        return response.jsonPath()
            .getString("access_token");
    }

}
