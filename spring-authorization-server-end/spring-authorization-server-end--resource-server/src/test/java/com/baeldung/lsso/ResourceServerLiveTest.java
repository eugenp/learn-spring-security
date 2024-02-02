package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/spring";
    private static final String AUTHORIZE_URL_PATTERN =
        AUTH_SERVER_BASE_URL + "/oauth2/authorize?response_type=code&client_id=lssoClient&scope=%s&state=1234&redirect_uri=" + REDIRECT_URL;

    private static final String TOKEN_URL = AUTH_SERVER_BASE_URL + "/oauth2/token";
    private static final String RESOURCE_URL = RESOURCE_SERVER_BASE_URL + "/lsso-resource-server/api/projects";

    @Test
    public void givenUserWithReadScope_whenGetProjectResource_thenSuccess() {
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
    public void givenUserWithReadScope_whenPostNewProjectResource_thenForbidden() {
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
    public void givenUserWithWriteScope_whenPostNewProjectResource_thenCreated() {
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

    private String obtainAccessToken(String scopes) {
        // redirect to login form
        Response response = RestAssured.given()
            .redirects()
            .follow(false)
            .get(String.format(AUTHORIZE_URL_PATTERN, scopes));
        String sessionId = response.getCookie("JSESSIONID");
        String kcPostAuthenticationUrl = "/login";

        // open login form
        response = RestAssured.given()
            .cookie("JSESSIONID", sessionId)
            .get(AUTH_SERVER_BASE_URL + kcPostAuthenticationUrl);

        Pattern pattern = Pattern.compile("<input.*name=\"_csrf\".*\\svalue=\"(.*?)\"\\s+/>");
        Matcher matcher = pattern.matcher(response.asString());
        String csrf = null;
        if (matcher.find()) {
            csrf = matcher.group(1);
        }

        // do login
        response = RestAssured.given()
            .cookie("JSESSIONID", sessionId)
            .formParams("username", USERNAME, "password", PASSWORD, "_csrf", csrf)
            .post(AUTH_SERVER_BASE_URL + kcPostAuthenticationUrl);
        assertThat(HttpStatus.FOUND.value()).isEqualTo(response.getStatusCode());

        // obtain authentication code and state
        String authorizeUrl = URLDecoder.decode(response.getHeader(HttpHeaders.LOCATION), StandardCharsets.UTF_8);
        sessionId = response.getCookie("JSESSIONID");
        response = RestAssured.given()
            .redirects()
            .follow(false)
            .cookie("JSESSIONID", sessionId)
            .get(authorizeUrl);

        // extract authorization code
        String location = response.getHeader(HttpHeaders.LOCATION);

        Map<String, String> queryParamsMap = Arrays.stream(location.substring(location.indexOf("?") + 1)
                .split("&"))
            .map(s -> s.split("="))
            .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

        String code = queryParamsMap.get("code");

        // get access token
        Charset charset = StandardCharsets.ISO_8859_1;
        String basicAuth = new String(Base64.getEncoder()
            .encode((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(charset)), charset);

        Map<String, String> params = new HashMap<>();
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
