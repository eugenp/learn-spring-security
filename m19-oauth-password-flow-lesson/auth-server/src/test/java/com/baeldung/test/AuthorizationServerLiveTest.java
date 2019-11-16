package com.baeldung.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
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
        final String tokenUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/oauth/token";

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("username", "user");
        params.put("password", "pass");

        Response response = RestAssured.given()
            .auth()
            .basic("lssClient", "lssSecret")
            .formParams(params)
            .post(tokenUrl);
        System.out.println(response.asString());
        assertTrue(response.asString()
            .contains("access_token"));
    }

}
