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

public class LiveTest {

    @Test
    public void givenImplicitGrant_whenObtainAccessToken_thenSuccess() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        final String port = "8081";
        final String redirectUrl = "http://www.example.com";
        final String authorizeUrl = "http://localhost:" + port + "/um-webapp/oauth/authorize";
        final String resourceUrl = "http://localhost:" + port + "/um-webapp/api/users";

        // user login
        Response response = RestAssured.given()
            .formParams("username", "john@test.com", "password", "123")
            .post("http://localhost:" + port + "/um-webapp/login");
        final String cookieValue = response.getCookie("JSESSIONID");
        System.out.println(cookieValue);

        // get access token
        final Map<String, String> params = new HashMap<String, String>();
        params.put("response_type", "token");
        params.put("client_id", "lssClient");
        params.put("redirect_uri", redirectUrl);
        response = RestAssured.given()
            .cookie("JSESSIONID", cookieValue)
            .formParams(params)
            .post(authorizeUrl);

        final String location = response.getHeader(HttpHeaders.LOCATION);
        System.out.println("Location => " + location);

        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        final String accessToken = location.split("#|=|&")[2];
        System.out.println("Access Token => " + accessToken);

        // Access resources using access token
        response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(resourceUrl);
        System.out.println(response.asString());
        assertTrue(response.as(List.class)
            .size() > 0);
    }

}
