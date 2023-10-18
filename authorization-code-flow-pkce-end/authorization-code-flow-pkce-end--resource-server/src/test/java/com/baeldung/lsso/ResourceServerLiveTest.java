package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;

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
    private static final String CLIENT_SECRET = "lssoSecret";

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String CLIENT_BASE_URL = "http://localhost:8082";
    private static final String AUTH_SERVER_BASE_URL = "http://localhost:8083";
    private static final String RESOURCE_SERVER_BASE_URL = "http://localhost:8081";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";
    private static final String AUTHORIZE_URL_PATTERN_WITHOUT_CODE_CHALLENGE = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=%s&redirect_uri=" + REDIRECT_URL;
    private static final String AUTHORIZE_URL_PATTERN = AUTHORIZE_URL_PATTERN_WITHOUT_CODE_CHALLENGE + "&code_challenge=%s&code_challenge_method=S256";
    private static final String TOKEN_URL = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/token";
    private static final String RESOURCE_URL = RESOURCE_SERVER_BASE_URL + "/lsso-resource-server/api/projects";

    @SuppressWarnings("unchecked")
    @Test
    public void givenUserWithReadScope_whenGetProjectResource_thenSuccess() throws NoSuchAlgorithmException {
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
    public void givenUserWithOtherScope_whenGetProjectResource_thenForbidden() throws NoSuchAlgorithmException {
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
    public void givenUserWithNonSupportedScope_whenObtainingAuthorizationCode_thenRedirectedWithErrorCode() {
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN_WITHOUT_CODE_CHALLENGE, "notSupported"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getHeader(HttpHeaders.LOCATION)).contains("error=invalid_request")
            .contains("error_description=Invalid+scopes%3A+notSupported");
    }

    @Test
    public void givenUserWithReadScope_whenPostNewProjectResource_thenForbidden() throws NoSuchAlgorithmException {
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
    public void givenUserWithWriteScope_whenPostNewProjectResource_thenCreated() throws NoSuchAlgorithmException {
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
    public void givenUserWithReadScopeAndWithoutCodeChallenge_whenObtainingAuthorizationCode_thenRedirectedWithErrorCode() {
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN_WITHOUT_CODE_CHALLENGE, "read"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getHeader(HttpHeaders.LOCATION)).contains("error=invalid_request")
            .contains("error_description=Missing+parameter%3A+code_challenge_method");
    }

    private String obtainAccessToken(String scopes) throws NoSuchAlgorithmException {
        final Map<String, String> pkceParams = generatePkceParameters();

        // obtain authentication url with custom codes
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, scopes, pkceParams.get(PkceParameterNames.CODE_CHALLENGE)));
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
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("redirect_uri", REDIRECT_URL);
        params.put("client_secret", CLIENT_SECRET);
        params.put(PkceParameterNames.CODE_VERIFIER, pkceParams.get(PkceParameterNames.CODE_VERIFIER));
        response = RestAssured.given()
            .formParams(params)
            .post(TOKEN_URL);
        return response.jsonPath()
            .getString("access_token");
    }

    private static Map<String, String> generatePkceParameters() throws NoSuchAlgorithmException {
        final Map<String, String> pkceParams = new HashMap<>();
        final StringKeyGenerator defaultSecureKeyGenerator = new Base64StringKeyGenerator(
            Base64.getUrlEncoder().withoutPadding(), 96);
        final String codeVerifier = defaultSecureKeyGenerator.generateKey();
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        final String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        pkceParams.put(PkceParameterNames.CODE_VERIFIER, codeVerifier);
        pkceParams.put(PkceParameterNames.CODE_CHALLENGE, codeChallenge);
        pkceParams.put(PkceParameterNames.CODE_CHALLENGE_METHOD, "S256");
        return pkceParams;
    }

}
