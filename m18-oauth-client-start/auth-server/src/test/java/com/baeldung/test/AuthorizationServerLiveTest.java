package com.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.baeldung.um.spring.LssApp2;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LssApp2.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthorizationServerLiveTest {

    @Autowired
    Environment environment;

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() {
        final String authServerport = environment.getProperty("local.server.port");
        final String redirectUrl = "http://www.example.com/";
        final String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssClient&redirect_uri=" + redirectUrl;
        final String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";

        // user login
        Response response = RestAssured.given()
            .formParams("username", "user", "password", "pass")
            .post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        final String cookieValue = response.getCookie("JSESSIONID");

        // get authorization code
        RestAssured.given()
            .cookie("JSESSIONID", cookieValue)
            .get(authorizeUrl);
        response = RestAssured.given()
            .cookie("JSESSIONID", cookieValue)
            .post(authorizeUrl);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        final String location = response.getHeader(HttpHeaders.LOCATION);
        assertTrue(location.contains(redirectUrl));
        System.out.println(location);
        final String code = location.substring(location.indexOf("code=") + 5);
        System.out.println(code);

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssClient");
        params.put("redirect_uri", redirectUrl);

        response = RestAssured.given()
            .auth()
            .basic("lssClient", "lssSecret")
            .formParams(params)
            .post(tokenUrl);
        System.out.println(response.asString());
        assertTrue(response.asString()
            .contains("access_token"));
    }

}
