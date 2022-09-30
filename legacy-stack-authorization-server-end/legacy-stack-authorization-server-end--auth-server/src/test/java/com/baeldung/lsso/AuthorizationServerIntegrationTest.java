package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthorizationServerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String CLIENT_ID = "lssoClient";
    private static final String CLIENT_SECRET = "lssoSecret";

    private static final String AUTH_SERVER_BASE_PATTERN = "http://localhost:%s";
    private static final String CLIENT_BASE_URL = "http://localhost:8082";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";
    private static final String AUTHORIZE_URL = "/oauth/authorize?response_type=code&client_id=lssoClient&scope=read&redirect_uri=" + REDIRECT_URL;
    private static final String TOKEN_URL = "/oauth/token";

    private String authServerBaseUrl;

    @BeforeEach
    public void setup() {
        authServerBaseUrl = String.format(AUTH_SERVER_BASE_PATTERN, serverPort);
    }

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() {
        String accessToken = obtainAccessToken();

        assertThat(accessToken).isNotBlank();
    }

    @Test
    public void whenServiceStarts_thenKeysEndpointIsAvailable() {
        final String keysUrl = authServerBaseUrl + "/endpoint/jwks.json";

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
            .get(authServerBaseUrl + AUTHORIZE_URL);
        String authSessionId = response.getCookie("JSESSIONID");
        String kcPostAuthenticationUrl = authServerBaseUrl + "/login";

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
            .post(authServerBaseUrl + TOKEN_URL);
        return response.jsonPath()
            .getString("access_token");
    }

}
