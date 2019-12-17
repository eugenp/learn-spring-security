package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This Live Test requires:
 * - the Authorization Server to be running
 * - the Resource Server to be running
 *
 */
public class ResourceServerLiveTest {

    private final String redirectUrl = "http://localhost:8082/lsso-client/login/oauth2/code/custom";
    private final String authorizeUrl = "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth?response_type=code&client_id=lssoClient&scope=read&redirect_uri=" + redirectUrl;
    private final String tokenUrl = "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token";
    private final String resourceUrl = "http://localhost:8081/lsso-resource-server/api/projects";
    private final String userInfoResourceUrl = "http://localhost:8081/lsso-resource-server/user/info";

    @SuppressWarnings("unchecked")
    @Test
    public void givenAccessToken_whenGetProjectResource_thenSuccess() {
        String accessToken = obtainAccessToken();
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(resourceUrl);
        System.out.println(response.asString());
        assertThat(response.as(List.class)).hasSizeGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void givenAccessToken_whenGetUserInformationResource_thenSuccess() {
        String accessToken = obtainAccessToken();
        System.out.println("ACCESS TOKEN: " + accessToken);

        // Access resources using access token
        Response response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get(userInfoResourceUrl);
        System.out.println(response.asString());
        assertThat(response.as(Map.class)).containsEntry("user_name", "john@test.com");
    }

    private String obtainAccessToken() {
        // obtain authentication url with custom codes
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(authorizeUrl);
        String authSessionId = response.getCookie("AUTH_SESSION_ID");
        String kcPostAuthenticationUrl = response.asString()
            .split("action=\"")[1].split("\"")[0].replace("&amp;", "&");

        // obtain authentication code and state
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("AUTH_SESSION_ID", authSessionId)
            .formParams("username", "john@test.com", "password", "123", "credentialId", "")
            .post(kcPostAuthenticationUrl);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        // extract authorization code
        String location = response.getHeader(HttpHeaders.LOCATION);
        String code = location.split("code=")[1].split("&")[0];

        // get access token
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", "lssoClient");
        params.put("redirect_uri", redirectUrl);
        params.put("client_secret", "lssoSecret");
        response = RestAssured.given()
            .formParams(params)
            .post(tokenUrl);
        return response.jsonPath()
            .getString("access_token");
    }

}
