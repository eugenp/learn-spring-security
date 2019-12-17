package com.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class JwkEndpointLiveTest {

    @Test
    public void whenAcessJwkEndpoint_thenSuccess() {
        final String authServerport = "8083";
        final String endpointUrl = "http://localhost:" + authServerport + "/um-webapp-auth-server/endpoint/jwks.json";

        Response response = RestAssured.get(endpointUrl);
        System.out.println(response.asString());
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        List keys = response.jsonPath()
            .getList("keys");
        assertTrue(keys.size() > 0);
    }

}
