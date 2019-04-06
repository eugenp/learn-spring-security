package com.baeldung.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.baeldung.um.web.model.Foo;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ResourceServerLiveTest {

    @Test
    public void givenAccessToken_whenGetUserResource_thenSuccess() {
        String accessToken = obtainAccessToken();

        // Access resources using access token
        Response response = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .get("http://localhost:8081/um-webapp-resource-server/api/foos/1");

        System.out.println(response.asString());

        Foo foo = response.as(Foo.class);
        assertTrue(foo.getName()
            .length() > 0);
    }

    private String obtainAccessToken() {
        // Obtain access token
        Response response = RestAssured.given()
            .auth()
            .basic("lssClient", "lssSecret")
            .formParams("grant_type", "password", "username", "user", "password", "pass")
            .post("http://localhost:8083/um-webapp-auth-server/oauth/token");

        System.out.println(response.asString());

        return response.jsonPath()
            .getString("access_token");
    }

}
