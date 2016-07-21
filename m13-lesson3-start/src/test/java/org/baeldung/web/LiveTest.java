package org.baeldung.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class LiveTest {

    static String APP_ROOT = "http://localhost:8081/um-webapp";

    @Test
    public void whenObtainAccessToken_thenOK() {
        final Response response = RestAssured.given().auth().preemptive().basic("lssClient", "lssSecret").with().formParam("grant_type", "client_credentials").post(APP_ROOT + "/oauth/token");

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("access_token"));
    }

    // == utility

}
