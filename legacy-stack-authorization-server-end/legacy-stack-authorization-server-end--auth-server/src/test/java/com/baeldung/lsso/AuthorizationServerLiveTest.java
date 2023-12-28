package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
 */
public class AuthorizationServerLiveTest {

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String CLIENT_ID = "lssoClient";
    private static final String CLIENT_SECRET = "lssoSecret";

    private static final String AUTH_SERVER_BASE_URL = "http://localhost:8083";
    private static final String CLIENT_BASE_URL = "http://localhost:8082";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";
    private static final String AUTHORIZE_URL =
        AUTH_SERVER_BASE_URL + "/oauth/authorize?response_type=code&client_id=lssoClient&scope=read&redirect_uri=" + REDIRECT_URL;
    private static final String TOKEN_URL = AUTH_SERVER_BASE_URL + "/oauth/token";

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() {
        String accessToken = obtainAccessToken();

        assertThat(accessToken).isNotBlank();
    }

    @Test
    public void whenServiceStarts_thenKeysEndpointIsAvailable() {
        final String keysUrl = AUTH_SERVER_BASE_URL + "/endpoint/jwks.json";

        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(keysUrl);

        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());
        System.out.println(response.asString());
        assertThat(response.jsonPath()
            .getMap("$.")).containsKeys("keys");
    }

    private String obtainAccessToken() {
        // obtain authentication url with custom codes
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(AUTHORIZE_URL);
        String authSessionId = response.getCookie("JSESSIONID");
        String kcPostAuthenticationUrl = AUTH_SERVER_BASE_URL + "/login";

        // open login form
        response = RestAssured.given()
            .cookie("JSESSIONID", authSessionId)
            .get(kcPostAuthenticationUrl);

        String csrf = response.asString()
            .split("value=\"")[1].split("\"")[0];

        // obtain authentication code and state
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", authSessionId)
            .formParams("username", USERNAME, "password", PASSWORD, "_csrf", csrf)
            .post(kcPostAuthenticationUrl);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        String location = URLDecoder.decode(response.getHeader(HttpHeaders.LOCATION), Charset.forName("UTF-8"));
        authSessionId = response.getCookie("JSESSIONID");

        // redirect to client url
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", authSessionId)
            .get(location);

        System.out.println(response.asString());
        // extract authorization code
        location = response.getHeader(HttpHeaders.LOCATION);
        String code = location.split("code=")[1].split("&")[0];

        // get access token
        Charset charset = StandardCharsets.ISO_8859_1;
        String basicAuth = new String(Base64.getEncoder()
            .encode((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(charset)), charset);

        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("redirect_uri", REDIRECT_URL);
        response = RestAssured.given()
            .header("Authorization", "Basic " + basicAuth)
            .queryParams(params)
            .post(TOKEN_URL);
        return response.jsonPath()
            .getString("access_token");
    }

}
