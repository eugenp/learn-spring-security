package com.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Oauth2ClientLiveTest {

    final String redirectUrl = "http://localhost:8082/um-webapp-client/login/oauth2/code/custom";
    final String authorizeUrl = "http://localhost:8083/um-webapp-auth-server/oauth/authorize?response_type=code&client_id=lssClient&scope=read&redirect_uri="+redirectUrl;
    final String tokenUrl = "http://localhost:8083/um-webapp-auth-server/oauth/token";
    final String resourceUrl = "http://localhost:8082/um-webapp-client/foos/1";
    final String clientAuthorizationUri = "http://localhost:8082/um-webapp-client/oauth2/authorization/custom";
    final String authServerLoginUri = "http://localhost:8083/um-webapp-auth-server/login";

    @Test
    public void givenAuthorizationCodeGrant_whenLoginUsingOauth_thenSuccess() throws UnsupportedEncodingException  {
        // authorization server login
        Response response = RestAssured.given().formParams("username", "john@test.com", "password", "123").post(authServerLoginUri);
        final String authServerSessionId = response.getCookie("JSESSIONID");
        
       
        // invoke authorization request on client to obtain state and session-id 
        response = RestAssured.given().redirects().follow(false).get(clientAuthorizationUri);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        String fullAuthorizeUrl = response.getHeader(HttpHeaders.LOCATION);
        assertTrue(fullAuthorizeUrl.contains("state"));
        
        // extract state from redirect uri
        int stateStart = fullAuthorizeUrl.indexOf("state=")+5;
        int stateEnd = fullAuthorizeUrl.indexOf("&",stateStart);
        final String state = URLDecoder.decode(fullAuthorizeUrl.substring(stateStart, stateEnd), "UTF-8");
        
        // extract client session-id as the quthorize request details are stored in HttpSession
        String clientSessionId = response.getCookie("JSESSIONID");

        // obtain authorization code
        RestAssured.given().redirects().follow(false).cookie("JSESSIONID", authServerSessionId).get(authorizeUrl+"&state="+state);
        response = RestAssured.given().redirects().follow(false).cookie("JSESSIONID", authServerSessionId).post(authorizeUrl+"&state="+state);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        
        // extract authorization code
        String location = response.getHeader(HttpHeaders.LOCATION);
        int codeStart = location.indexOf("code=") + 5; 
        String code = location.substring(codeStart, location.indexOf("&"));
        
        // mimic oauth2login
        response = RestAssured.given().redirects().follow(false).cookie("JSESSIONID", clientSessionId).get(redirectUrl+"?code="+code+"&state="+state);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        
        // extract new client session-id after authentication
        String newClientSessionId = response.getCookie("JSESSIONID");
        response = RestAssured.given().cookie("JSESSIONID", newClientSessionId).get(resourceUrl);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());        
        assertTrue(response.asString().contains("Foo Details"));        
        System.out.println(response.asString());
    }
    


}
