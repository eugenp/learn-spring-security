package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
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
    private static final String CLIENT_SECRET = "lssoSecret";

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String CLIENT_BASE_URL = "http://localhost:8082";
    private static final String AUTH_SERVER_BASE_URL = "http://localhost:8083";
    private static final String RESOURCE_SERVER_BASE_URL = "http://localhost:8081";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";
    private static final String AUTHORIZE_URL_PATTERN = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=%s&redirect_uri=" + REDIRECT_URL;
    private static final String TOKEN_URL = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/token";
    private static final String RESOURCE_URL = RESOURCE_SERVER_BASE_URL + "/lsso-resource-server/api/projects";
    private static final String INTROSPECTION_URL = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/token/introspect";
    private static final String REVOKE_TOKEN_URL = AUTH_SERVER_BASE_URL + "/auth/realms/baeldung/protocol/openid-connect/revoke";

    @SuppressWarnings("unchecked")
    @Test
    public void givenUserWithReadScope_whenGetProjectResource_thenSuccess() {
        String accessToken = obtainTokens("read").get("accessToken");
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(RESOURCE_URL);
        System.out.println(response.asString());
        assertThat(response.as(List.class)).hasSizeGreaterThan(0);
    }

    @Test
    public void givenUserWithOtherScope_whenGetProjectResource_thenForbidden() {
        String accessToken = obtainTokens("email").get("accessToken");
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserWithNonSupportedScope_whenObtainingAuthorizationCode_thenRedirectedWithErrorCode() {
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, "notSupported"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.value());
        assertThat(response.getHeader(HttpHeaders.LOCATION)).contains("error=invalid_request")
            .contains("error_description=Invalid+scopes%3A+notSupported");
    }

    @Test
    public void givenUserWithReadScope_whenPostNewProjectResource_thenForbidden() {
        String accessToken = obtainTokens("read").get("accessToken");
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
    public void givenUserWithWriteScope_whenPostNewProjectResource_thenCreated() {
        String accessToken = obtainTokens("read write").get("accessToken");
        System.out.println("ACCESS TOKEN: " + accessToken);
        ProjectDto newProject = new ProjectDto(null, "newProject", LocalDate.now());

        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .body(newProject)
            .log()
            .all()
            .post(RESOURCE_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void givenLoggedInUser_whenTokenRevoked_thenUnAuthorized() {
        Map<String, String> tokenMap = obtainTokens("read write");
        Map<String, String> introspectionParams = new HashMap<>();
        introspectionParams.put("token_type_hint", "requesting_party_token");
        introspectionParams.put("client_id", CLIENT_ID);
        introspectionParams.put("client_secret", CLIENT_SECRET);
        introspectionParams.put("token", tokenMap.get("accessToken"));
        RestAssured.given()
            .formParams(introspectionParams)
            .post(INTROSPECTION_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("active", Matchers.is(true));

        // Revoke refresh token
        Map<String, String> revocationParams = new HashMap<>();
        revocationParams.put("client_id", CLIENT_ID);
        revocationParams.put("client_secret", CLIENT_SECRET);
        revocationParams.put("token_type_hint", "refresh_token");
        revocationParams.put("token", tokenMap.get("refreshToken"));
        RestAssured.given()
            .formParams(revocationParams)
            .post(REVOKE_TOKEN_URL);

        // Ensure that both refresh token and access token are now invalid
        RestAssured.given()
            .formParams(introspectionParams) // With access token
            .post(INTROSPECTION_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("active", Matchers.is(false));

        // Replace access token with refresh token
        introspectionParams.put("token", tokenMap.get("refreshToken"));
        RestAssured.given()
            .formParams(introspectionParams) // With access token
            .post(INTROSPECTION_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("active", Matchers.is(false));

    }

    private Map<String, String> obtainTokens(String scopes) {
        // obtain authentication url with custom codes
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, scopes));
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
        response = RestAssured.given()
            .formParams(params)
            .post(TOKEN_URL);
        String accessToken = response.jsonPath()
            .getString("access_token");
        String refreshToken = response.jsonPath()
            .getString("refresh_token");
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        return tokenMap;
    }

}
