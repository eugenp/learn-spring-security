package com.baeldung.lsso;

import com.baeldung.lsso.persistence.model.Project;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenLiveTest {

    private static final String AUTH_SERVICE_BASE_URL = "http://localhost:8083/auth/realms/baeldung";
    private static final String CONNECT_TOKEN = AUTH_SERVICE_BASE_URL + "/protocol/openid-connect/token";
    private static final String SERVER_API_PROJECTS = "http://localhost:8081/lsso-resource-server/api/projects";

    @Test
    public void givenClientCredentialsGrant_whenUseToken_thenSuccess() {
        final Map<String, String> params = new HashMap<>();
        params.put("scope", "read write");
        params.put("grant_type", "client_credentials");
        params.put("client_id", "lssoClient");
        params.put("client_secret", "lssoSecret");

        Response response = RestAssured.given().formParams(params).post(CONNECT_TOKEN);

        String accessToken = response.jsonPath().getString("access_token");

        assertThat(accessToken).isNotBlank();

        response = RestAssured.given().auth().oauth2(accessToken).get(SERVER_API_PROJECTS);

        assertThat(HttpStatus.OK.value()).isEqualTo(response.getStatusCode());

        List<Project> projects = response.getBody().as(List.class);
        assertThat(3).isEqualTo(projects.size());
    }
}
