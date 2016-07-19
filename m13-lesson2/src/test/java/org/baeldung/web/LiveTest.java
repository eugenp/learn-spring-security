package org.baeldung.web;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void givenReadAndWriteScope_whenAccessUsers_thenOK() {
        final String token = obtainAccessToken("lssClient", "lssSecret");
        final Response readResponse = RestAssured.given().header("Authorization", "Bearer " + token).get(APP_ROOT + "/api/user");
        assertEquals(200, readResponse.getStatusCode());

        final Map<String, String> params = createRandomUser();
        final Response writeResponse = RestAssured.given().header("Authorization", "Bearer " + token).formParameters(params).post(APP_ROOT + "/api/user");
        assertEquals(201, writeResponse.getStatusCode());
    }

    @Test
    public void givenReadScope_whenAccessUsers_thenCanReadOnly() {
        final String token = obtainAccessToken("lssReadOnly", "lssReadSecret");
        final Response readResponse = RestAssured.given().header("Authorization", "Bearer " + token).get(APP_ROOT + "/api/user");
        assertEquals(200, readResponse.getStatusCode());

        final Map<String, String> params = createRandomUser();
        final Response writeResponse = RestAssured.given().header("Authorization", "Bearer " + token).formParameters(params).post(APP_ROOT + "/api/user");
        assertEquals(403, writeResponse.getStatusCode());
    }

    @Test
    public void givenWriteScope_whenAccessUsers_thenCanWriteOnly() {
        final String token = obtainAccessToken("lssWriteOnly", "lssWriteSecret");
        final Response readResponse = RestAssured.given().header("Authorization", "Bearer " + token).get(APP_ROOT + "/api/user");
        assertEquals(403, readResponse.getStatusCode());

        final Map<String, String> params = createRandomUser();
        final Response writeResponse = RestAssured.given().header("Authorization", "Bearer " + token).formParameters(params).post(APP_ROOT + "/api/user");
        assertEquals(201, writeResponse.getStatusCode());
    }

    // == utility

    private String obtainAccessToken(String clientId, String secret) {
        final Response response = RestAssured.given().auth().preemptive().basic(clientId, secret).with().formParam("grant_type", "client_credentials").post(APP_ROOT + "/oauth/token");
        return response.jsonPath().getString("access_token");
    }

    private Map<String, String> createRandomUser() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("email", randomAlphabetic(4) + "@test.com");
        final String password = randomAlphabetic(8);
        params.put("password", password);
        params.put("passwordConfirmation", password);
        return params;
    }

}
