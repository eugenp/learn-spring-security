package com.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class AuthorizationCodeLiveTest {

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
    	final String resourceServerport = "8081";
        final String authServerport = "8083";
        final String redirectUrl = "http://www.example.com";
        final String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssClient&redirect_uri=" + redirectUrl;
        final String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";
        final String resourceUrl = "http://localhost:" + resourceServerport + "/um-webapp-resource-server/api/users";

        // user login
        Response response = RestAssured.given().formParams("username", "john@test.com", "password", "123").post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        final String cookieValue = response.getCookie("JSESSIONID");

        // get authorization code
        RestAssured.given().cookie("JSESSIONID", cookieValue).get(authorizeUrl);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_oauth_approval", "true");
        params.put("authorize", "Authorize");
        params.put("scope.read", "true");
        response = RestAssured.given().cookie("JSESSIONID", cookieValue).formParams(params).post(authorizeUrl);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        final String location = response.getHeader(HttpHeaders.LOCATION);
        assertTrue(location.contains(redirectUrl));
        System.out.println(location);
        final String code = location.substring(location.indexOf("code=") + 5);
        System.out.println(code);

        // get access token
        params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssClient");
        params.put("redirect_uri", redirectUrl);

        response = RestAssured.given().auth().basic("lssClient", "lssSecret").formParams(params).post(tokenUrl);
        System.out.println(response.asString());
        final String accessToken = response.jsonPath().getString("access_token");

        // Access resources using access token
        response = RestAssured.given().header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).get(resourceUrl);
        System.out.println(response.asString());
        assertTrue(response.as(List.class).size() > 0);
    }

}
