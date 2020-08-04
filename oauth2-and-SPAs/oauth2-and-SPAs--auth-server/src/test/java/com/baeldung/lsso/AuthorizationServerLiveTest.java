package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    private static final String AUTH_SERVER_BASE_URL = "http://localhost:8083/auth/realms/baeldung";
    private static final String CLIENT_BASE_URL = "http://localhost:8082";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/modal-code-handler.html";
    private static final String AUTHORIZE_URL_PATTERN = AUTH_SERVER_BASE_URL + "/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=%s&code_challenge_method=S256&code_challenge=%s&redirect_uri=" + REDIRECT_URL;
    private static final String TOKEN_URL = AUTH_SERVER_BASE_URL + "/protocol/openid-connect/token";

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() throws Exception {
        String accessToken = obtainAccessToken("read");

        assertThat(accessToken).isNotBlank();
    }

    @Test
    public void whenServiceStartsAndLoadsRealmConfigurations_thenOidcDiscoveryEndpointIsAvailable() {
        final String oidcDiscoveryUrl = AUTH_SERVER_BASE_URL + "/.well-known/openid-configuration";

        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(oidcDiscoveryUrl);

        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());
        System.out.println(response.asString());
        assertThat(response.jsonPath()
            .getMap("$.")).containsKeys("issuer", "authorization_endpoint", "token_endpoint", "userinfo_endpoint");
    }

    @Test
    public void givenInvalidCodeChallenge_whenObtainingAuthorizationCode_thenRedirectedWithErrorCode() {
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, "read", "invalidCodeChallenge"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getHeader(HttpHeaders.LOCATION)).contains("error=invalid_request")
            .contains("error_description=Invalid+parameter%3A+code_challenge");
    }

    @Test
    public void givenInvalidCodeValidator_whenObtainingAccessToken_thenBadRequestResponse() throws Exception {
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, "read", codeChallenge));

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
        params.put("code_verifier", "invalidCodeVerifier");
        response = RestAssured.given()
            .formParams(params)
            .post(TOKEN_URL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath()
            .getString("error")).isEqualTo("invalid_grant");
        assertThat(response.jsonPath()
            .getString("error_description")).isEqualTo("PKCE invalid code verifier");
    }

    private String obtainAccessToken(String scopes) throws Exception {
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        // obtain authentication url with custom codes
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, scopes, codeChallenge));
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
        params.put("code_verifier", codeVerifier);
        response = RestAssured.given()
            .formParams(params)
            .post(TOKEN_URL);
        return response.jsonPath()
            .getString("access_token");
    }

    private String generateCodeVerifier() {
        byte[] codeVerifierBytes = new byte[32];
        new Random().nextBytes(codeVerifierBytes);
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(codeVerifierBytes);
    }

    private String generateCodeChallenge(String codeVerifier) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] codeChallengeBytes = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(codeChallengeBytes);
    }

}
