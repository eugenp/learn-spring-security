package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.baeldung.lsso.web.dto.ProjectDto;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * This Live Test requires:
 * - the Authorization Server to be running
 * - the Resource Server to be running
 *
 */
public class ResourceServerLiveTest {

    private static final String CLIENT_ID = "lssoClient";

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String CLIENT_BASE_URL = "http://localhost:8082";
    private static final String AUTH_SERVER_BASE_URL = "http://localhost:8083";
    private static final String RESOURCE_SERVER_BASE_URL = "http://localhost:8081";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/modal-code-handler.html";
    private static final String AUTHORIZE_URL_PATTERN = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=%s&code_challenge_method=S256&code_challenge=%s&redirect_uri=" + REDIRECT_URL;
    private static final String TOKEN_URL = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/token";
    private static final String RESOURCE_URL = RESOURCE_SERVER_BASE_URL + "/lsso-resource-server/api/projects";

    @SuppressWarnings("unchecked")
    @Test
    public void givenUserWithReadScope_whenGetProjectResource_thenSuccess() throws Exception {
        String accessToken = obtainAccessToken("read");
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(RESOURCE_URL);
        System.out.println(response.asString());
        assertThat(response.as(List.class)).hasSizeGreaterThan(0);
    }

    @Test
    public void givenUserWithOtherScope_whenGetProjectResource_thenForbidden() throws Exception {
        String accessToken = obtainAccessToken("email");
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(RESOURCE_URL);
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserWithNonSupportedScope_whenObtainingAuthorizationCode_thenRedirectedWithErrorCode() throws Exception {
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, "notSupported", codeChallenge));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getHeader(HttpHeaders.LOCATION)).contains("error=invalid_request")
            .contains("error_description=Invalid+scopes%3A+notSupported");
    }

    @Test
    public void givenUserWithReadScope_whenPostNewProjectResource_thenForbidden() throws Exception {
        String accessToken = obtainAccessToken("read");
        System.out.println("ACCESS TOKEN: " + accessToken);
        ProjectDto newProject = new ProjectDto(null, "newProject", LocalDate.now());

        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .body(newProject)
            .post(RESOURCE_URL);
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserWithWriteScope_whenPostNewProjectResource_thenCreated() throws Exception {
        String accessToken = obtainAccessToken("read write");
        System.out.println("ACCESS TOKEN: " + accessToken);
        ProjectDto newProject = new ProjectDto(null, "newProject", LocalDate.now());

        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .body(newProject)
            .log()
            .all()
            .post(RESOURCE_URL);
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
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
