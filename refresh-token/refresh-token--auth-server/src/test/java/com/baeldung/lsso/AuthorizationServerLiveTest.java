package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Needs the following to be running: 
 * - Authorization Server
 * - Client
 * - Resource Server
 */
public class AuthorizationServerLiveTest {

    private static final String CLIENT_ID = "lssoClient";
    private static final String CLIENT_SECRET = "lssoSecret";

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String RESOURCE_SERVER_BASE_URL = "http://localhost:8081";
    private static final String CLIENT_BASE_URL = "http://localhost:8082";
    private static final String AUTH_SERVER_BASE_URL = "http://localhost:8083";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";
    private static final String AUTHORIZE_URL = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=read&redirect_uri=" + REDIRECT_URL;
    private static final String TOKEN_URL = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/token";
    private static final String RESOURCE_URL = RESOURCE_SERVER_BASE_URL + "/lsso-resource-server/api/projects";

    @Test
    public void givenAccessToken_whenRefreshToken_thenSuccess() {
        Response response = obtainTokens();
        String refreshToken = response.jsonPath()
            .getString("refresh_token");

        assertThat(refreshToken).isNotBlank();

        // refresh access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("refresh_token", refreshToken);
        String accessToken = RestAssured.given()
            .formParams(params)
            .post(TOKEN_URL)
            .jsonPath()
            .getString("access_token");

        assertThat(accessToken).isNotBlank();

        Response resourceServerResponse = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .get(RESOURCE_URL);

        assertThat(HttpStatus.OK.value()).isEqualTo(resourceServerResponse.getStatusCode());
    }

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() {
        Response response = obtainTokens();

        assertThat(response.jsonPath()
            .getString("access_token")).isNotBlank();
    }

    @Test
    public void whenServiceStartsAndLoadsRealmConfigurations_thenOidcDiscoveryEndpointIsAvailable() {
        final String oidcDiscoveryUrl = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/.well-known/openid-configuration";

        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(oidcDiscoveryUrl);

        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());
        System.out.println(response.asString());
        assertThat(response.jsonPath()
            .getMap("$.")).containsKeys("issuer", "authorization_endpoint", "token_endpoint", "userinfo_endpoint");
    }

    private Response obtainTokens() {
        // obtain authentication url with custom codes
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(AUTHORIZE_URL);
        String authSessionId = response.getCookie("AUTH_SESSION_ID");
        String kcPostAuthenticationUrl = response.asString()
            .split("action=\"")[1].split("\"")[0].replace("&amp;", "&");

        // obtain authentication code and state
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("AUTH_SESSION_ID", authSessionId)
            .formParams("username", USERNAME, "password", PASSWORD, "credentialId", "")
            .post(kcPostAuthenticationUrl);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        // extract authorization code
        String location = response.getHeader(HttpHeaders.LOCATION);
        String code = location.split("code=")[1].split("&")[0];

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("redirect_uri", REDIRECT_URL);
        params.put("client_secret", CLIENT_SECRET);
        return RestAssured.given()
            .formParams(params)
            .post(TOKEN_URL);
    }

}
