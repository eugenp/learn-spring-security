package com.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.baeldung.um.spring.LssoApp;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LssoApp.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthorizationServerIntegrationTest {

    @LocalServerPort
    String authServerport;

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() {
        String redirectUrl = "http://localhost:8082/um-webapp-client/login/oauth2/code/custom";
        final String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssoClient&redirect_uri=" + redirectUrl;
        final String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";

        // user login
        Response response = RestAssured.given()
            .formParams("username", "john@test.com", "password", "123")
            .post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        final String cookieValue = response.getCookie("JSESSIONID");

        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", cookieValue)
            .post(authorizeUrl);
        assertEquals(HttpStatus.SEE_OTHER.value(), response.getStatusCode());
        final String location = response.getHeader(HttpHeaders.LOCATION);
        assertTrue(location.contains(redirectUrl));
        System.out.println(location);
        final String code = location.substring(location.indexOf("code=") + 5);
        System.out.println(code);

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
        System.out.println(response.asString());
        assertTrue(response.asString()
            .contains("access_token"));
    }

}
