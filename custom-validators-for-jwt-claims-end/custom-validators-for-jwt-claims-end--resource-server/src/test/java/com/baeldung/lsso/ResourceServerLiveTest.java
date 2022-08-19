package com.baeldung.lsso;

import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This Live Test requires:
 * - the Authorization Server to be running on port 8083
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class ResourceServerLiveTest {

    private static final String CLIENT_ID = "lssoClient";
    private static final String CLIENT_SECRET = "lssoSecret";

    private static final String USER_WITH_VALID_CUSTOM_CLAIM = "john@test.com";
    private static final String USER_WITH_INVALID_CUSTOM_CLAIM = "mike@other.com";

    private static final String AUTH_SERVER = "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect";

    private static final String REDIRECT_URL = "http://localhost:8082/lsso-client/login/oauth2/code/custom";
    private static final String RESOURCE_URL = "http://localhost:{port}/lsso-resource-server/api/projects/1";

    @LocalServerPort
    private int port;

    @Test
    public void givenValidUser_whenValidCustomClaim_thenAllowedForProjectResource() {
        String accessToken = obtainAccessTokenWithAuthorizationCode(USER_WITH_VALID_CUSTOM_CLAIM, "123");

        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .get(resourceUrl())
            .then().assertThat()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void givenValidUser_whenInvalidCustomClaim_thenUnauthorizedForProjectResource() {
        String accessToken = obtainAccessTokenWithAuthorizationCode(USER_WITH_INVALID_CUSTOM_CLAIM, "pass");

        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .get(resourceUrl())
            .then().assertThat()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .header("WWW-Authenticate", stringContainsInOrder("invalid_token", "Only @test.com users are allowed access"));
    }

    private String resourceUrl() {
        return RESOURCE_URL.replace("{port}", port + "");
    }

    private String obtainAccessTokenWithAuthorizationCode(String username, String password) {

        String authorizeUrl = AUTH_SERVER + "/auth";

        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("client_id", CLIENT_ID);
        loginParams.put("response_type", "code");
        loginParams.put("redirect_uri", REDIRECT_URL);
        loginParams.put("scope", "read write");

        // user login
        Response response = RestAssured.given()
            .formParams(loginParams)
            .get(authorizeUrl);
        String cookieValue = response.getCookie("AUTH_SESSION_ID");

        String authUrlWithCode = response.htmlPath()
            .getString("'**'.find{node -> node.name()=='form'}*.@action");

        // get code
        Map<String, String> codeParams = new HashMap<String, String>();
        codeParams.put("username", username);
        codeParams.put("password", password);
        response = RestAssured.given()
            .cookie("AUTH_SESSION_ID", cookieValue)
            .formParams(codeParams)
            .post(authUrlWithCode);

        String location = response.getHeader(HttpHeaders.LOCATION);

        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        final String code = location.split("#|=|&")[3];

        //get access token
        Map<String, String> tokenParams = new HashMap<String, String>();
        tokenParams.put("grant_type", "authorization_code");
        tokenParams.put("client_id", CLIENT_ID);
        tokenParams.put("client_secret", CLIENT_SECRET);
        tokenParams.put("redirect_uri", REDIRECT_URL);
        tokenParams.put("code", code);

        String tokenUrl = AUTH_SERVER + "/token";
        response = RestAssured.given()
            .formParams(tokenParams)
            .post(tokenUrl);

        return response.jsonPath()
            .getString("access_token");
    }

}
