package com.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class RefreshTokenLiveTest {

    String authServerport = "8083";
    String redirectUrl = "http://www.example.com/";
    String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssClient&redirect_uri=" + redirectUrl;
    String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";

    @Test
    public void givenRefreshToken_whenObtainAccessToken_thenSuccess() throws JsonParseException, JsonMappingException, IOException {
        String username = "user";
        String password = "pass";
        OAuth2AccessToken accessToken = obtainAccessTokenUsingAuthCode(username,password);
        assertNotNull(accessToken.getRefreshToken());
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", accessToken.getRefreshToken().getValue());
        Response response = RestAssured.given().auth().basic("lssClient", "lssSecret").formParams(params).post(tokenUrl);
        System.out.println(response.asString());
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("access_token"));
    }
    
    private OAuth2AccessToken obtainAccessTokenUsingAuthCode(String username, String password) {
        // user login
        Response response = RestAssured.given().formParams("username", username, "password", password).post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        final String cookieValue = response.getCookie("JSESSIONID");

        // get authorization code
        RestAssured.given().cookie("JSESSIONID", cookieValue).get(authorizeUrl); 
        response = RestAssured.given().cookie("JSESSIONID", cookieValue).post(authorizeUrl);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        final String location = response.getHeader(HttpHeaders.LOCATION);
        final String code = location.substring(location.indexOf("code=") + 5);

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssClient");
        params.put("redirect_uri", redirectUrl);

        response = RestAssured.given().auth().basic("lssClient", "lssSecret").formParams(params).post(tokenUrl);
        System.out.println(response.asString());
        return response.as(OAuth2AccessToken.class);
    }

}
