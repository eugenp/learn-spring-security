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
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class JwtTokenLiveTest {

    @Test
    public void whenObtainJwtAccessToken_thenSuccess() throws JsonParseException, JsonMappingException, IOException {
        String username = "john@test.com";
        String password = "123";
        String accessToken = obtainAccessToken(username, password);
        assertNotNull(accessToken);
        Jwt jwt = JwtHelper.decode(accessToken);
        System.out.println(jwt.getClaims());
        Map<String, String> claims = new ObjectMapper().readValue(jwt.getClaims(), Map.class);
        assertTrue(claims.containsKey("user_name"));
        assertEquals(claims.get("user_name"), username);
    }

    private String obtainAccessToken(String username, String password) {
        final String authServerport = "8083";
        final String redirectUrl = "http://www.example.com/";
        final String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssoClient&redirect_uri=" + redirectUrl;
        final String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";

        // user login
        Response response = RestAssured.given()
            .formParams("username", username, "password", password)
            .post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        final String cookieValue = response.getCookie("JSESSIONID");

        // get authorization code
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", cookieValue)
            .post(authorizeUrl);
        assertEquals(HttpStatus.SEE_OTHER.value(), response.getStatusCode());
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
