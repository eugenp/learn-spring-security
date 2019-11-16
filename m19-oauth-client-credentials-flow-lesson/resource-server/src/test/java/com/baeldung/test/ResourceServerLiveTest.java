package com.baeldung.test;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.baeldung.um.spring.LssApp2;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This test requires the Authorization Server to be up and running.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LssApp2.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ResourceServerLiveTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String AUTH_SERVER_PORT = "8083";
    private static final String TOKEN_URL = "http://localhost:" + AUTH_SERVER_PORT + "/um-webapp-auth-server/oauth/token";
    private static final String RESOURCE_ENDPOINT = "/api/foos/1";

    @Test
    public void givenAccessToken_whenGetUserResource_thenSuccess() throws Exception {
        String accessToken = obtainAccessToken();

        // Access resources using access token
        this.mockMvc.perform(get(RESOURCE_ENDPOINT).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(any(Long.class), Long.class))
            .andExpect(jsonPath("$.name", isA(String.class)));
    }

    private String obtainAccessToken() {
        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credentials");
        Response response = RestAssured.given()
            .auth()
            .basic("lssClient", "lssSecret")
            .formParams(params)
            .post(TOKEN_URL);
        return response.jsonPath()
            .getString("access_token");
    }

}
