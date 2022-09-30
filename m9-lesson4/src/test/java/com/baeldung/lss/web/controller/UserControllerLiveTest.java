package com.baeldung.lss.web.controller;

import static io.restassured.RestAssured.given;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UserControllerLiveTest {

    static {
        RestAssured.baseURI = "http://localhost:8081";
    }

    @Test
    public void givenAuthenticated_whenDeletingUser_thenUserDeleted() {
        final Response response = givenAuthenticated("admin", "pass").formParam("username", randomUsername())
            .formParam("email", randomEmail())
            .when()
            .post("/user/?form")
            .then()
            .statusCode(302)
            .extract()
            .response();

        final String locationHeader = response.getHeader("Location");
        final String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        givenAuthenticated("admin", "pass").when()
            .get("/user/delete/" + userId)
            .then()
            .statusCode(200);
    }

    // Private Helper Methods

    private String randomEmail() {
        return RandomStringUtils.randomNumeric(5) + "@email.com";
    }

    private String randomUsername() {
        return "test" + RandomStringUtils.randomNumeric(5);
    }

    private RequestSpecification givenAuthenticated(final String username, final String password) {
        return given().auth()
            .form(username, password, new FormAuthConfig("/doLogin", "username", "password"));
    }

}
