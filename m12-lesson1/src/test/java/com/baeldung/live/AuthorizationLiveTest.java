package com.baeldung.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class AuthorizationLiveTest {

    private String appURL = "http://localhost:8082/um-webapp";

    private Response obtainAccessToken(String clientId, String username, String password) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("username", username);
        params.put("password", password);
        return RestAssured.given().auth().preemptive().basic(clientId, "bGl2ZS10ZXN0").and().with().params(params).when().post(appURL + "/oauth/token");
    }

    @Test
    public void givenUser_whenGetToken_thenOk() {
        final Response response = obtainAccessToken("live-test", "admin@fake.com", "adminpass");
        assertEquals(200, response.getStatusCode());

        final String accessToken = response.jsonPath().getString("access_token");
        final Response userListResponse = RestAssured.given().header("Authorization", "Bearer " + accessToken).get(appURL + "/api/user");
        assertEquals(200, userListResponse.getStatusCode());
        assertNotNull(userListResponse.jsonPath().get("email"));

    }

}
