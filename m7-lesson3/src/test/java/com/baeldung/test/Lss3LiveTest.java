package com.baeldung.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Lss3LiveTest {

    private static String APP_ROOT = "http://localhost:8081";

    @Test
    public void givenUser_whenGetRunAsAPI_thenOkAndExpectedContent() {
        final Response response = givenAuth().get(APP_ROOT + "/runas");
        assertEquals(200, response.getStatusCode());
        assertEquals("Current User Authorities inside this RunAS method only [ROLE_RUN_AS_REPORTER, ROLE_USER]", response.getBody().asString());
    }

    private RequestSpecification givenAuth() {
        return RestAssured.given()
            .auth()
            .form("test@email.com", "pass");
    }

}