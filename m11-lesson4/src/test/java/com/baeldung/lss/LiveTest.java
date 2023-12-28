package com.baeldung.lss;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * This Test class relies on HSQL DB and all integration tests in this class are run against
 * in-memory HSQL DB instead of MySQL DB.
 */
public class LiveTest {

    static {
        RestAssured.baseURI = "http://localhost:8081";
    }

    @Test
    public void givenOwnerUser_whenGetPossession_thenOK() {
        final Response response = givenAuth("eugen@email.com", "pass").get("/possessions/2");
        assertEquals(200, response.getStatusCode());
        assertThat(response.body()
            .jsonPath()
            .getLong("id")).isEqualTo(2L);
    }

    @Test
    public void givenUserWithReadPermission_whenGetPossession_thenOK() {
        final Response response = givenAuth("eric@email.com", "123").get("/possessions/2");
        assertEquals(200, response.getStatusCode());
        assertThat(response.body()
            .jsonPath()
            .getLong("id")).isEqualTo(2L);
    }

    @Test
    public void givenUserWithNoPermission_whenGetPossession_thenForbidden() {
        final Response response = givenAuth("eugen@email.com", "pass").get("/possessions/3");
        assertEquals(403, response.getStatusCode());
    }

    //
    private RequestSpecification givenAuth(String username, String password) {
        return RestAssured.given()
                .auth()
                .form(username, password, new FormAuthConfig("/doLogin", "username", "password"));
    }
}