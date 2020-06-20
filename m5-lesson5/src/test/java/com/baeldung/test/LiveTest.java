package com.baeldung.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class LiveTest {

    private static String APP_ROOT = "http://localhost:8081";

    @Test
    public void givenUser_whenGetAllUsers_thenForbidden() {
        final Response response = givenAuth("user", "pass").get(APP_ROOT + "/user");

        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void givenAdmin_whenGetAllUsers_thenOK() {
        final Response response = givenAuth("admin", "pass").get(APP_ROOT + "/user");

        assertEquals(200, response.getStatusCode());
    }

    //

    private final RequestSpecification givenAuth(String username, String password) {
        return RestAssured.given()
            .auth()
            .preemptive()
            .basic(username, password);
    }

}