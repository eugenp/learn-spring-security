package com.baeldung.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import com.baeldung.um.web.model.Foo;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ResourceServerLiveTest {
    final String resourceServerport = "8081";
    final String authServerport = "8083";
    final String redirectUrl = "http://www.example.com/";
    final String authorizeUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssClient&redirect_uri=" + redirectUrl;
    final String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";
    final String resourceUrl = "http://localhost:" + resourceServerport + "/um-webapp-resource-server/api/foos/1";
    
    @Test
    public void givenAccessToken_whenGetUserResource_thenSuccess() {
        String accessToken = obtainAccessToken();

        // Access resources using access token
        Response response = RestAssured.given().header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).get(resourceUrl);
        System.out.println(response.asString());
        Foo foo = response.as(Foo.class);
        assertTrue(foo.getName().length()>0);
    }
    
    private String obtainAccessToken() {
        Response response = RestAssured.given().formParams("username", "john@test.com", "password", "123").post("http://localhost:" + authServerport + "/um-webapp-auth-server/login");
        final String cookieValue = response.getCookie("JSESSIONID");
        RestAssured.given().cookie("JSESSIONID", cookieValue).get(authorizeUrl); 
        response = RestAssured.given().cookie("JSESSIONID", cookieValue).post(authorizeUrl);
        final String location = response.getHeader(HttpHeaders.LOCATION);
        final String code = location.substring(location.indexOf("code=") + 5);

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssClient");
        params.put("redirect_uri", redirectUrl);
        response = RestAssured.given().auth().basic("lssClient", "lssSecret").formParams(params).post(tokenUrl);
        return response.jsonPath().getString("access_token");
    }

}
