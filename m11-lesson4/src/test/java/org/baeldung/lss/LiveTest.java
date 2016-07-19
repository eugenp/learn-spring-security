package org.baeldung.lss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class LiveTest {

    private static String APP_ROOT = "http://localhost:8081";
    private final FormAuthConfig formAuthConfig = new FormAuthConfig(APP_ROOT + "/doLogin", "username", "password");

    @Test
    public void givenOwnerUser_whenGetPossession_thenOK() {
        final Response response = givenAuth("bill@email.com", "pass").get(APP_ROOT + "/possessions/2");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    public void givenUserWithReadPermission_whenGetPossession_thenOK() {
        final Response response = givenAuth("john@email.com", "123").get(APP_ROOT + "/possessions/2");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    public void givenUserWithNoPermission_whenGetPossession_thenForbidden() {
        final Response response = givenAuth("bill@email.com", "pass").get(APP_ROOT + "/possessions/3");
        assertEquals(403, response.getStatusCode());
    }

    //
    private RequestSpecification givenAuth(String username, String password) {
        return RestAssured.given().auth().form(username, password, formAuthConfig);
    }
}