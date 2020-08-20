package com.baeldung.lsso;

import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

/**
 * This Live Test requires:
 * - the Project Resource Server to be running
 */
public class ProjectResourceServerLiveTest {

    private static final String PROJECT_RESOURCE_SERVER_BASE_URL = "http://localhost:8081/lsso-project-resource-server";

    private static final String PROJECT_RESOURCE_URL = PROJECT_RESOURCE_SERVER_BASE_URL + "/api/projects";

    @Test
    public void givenRequestWithPreAuthHeaders_whenRequestProjectsEndpoint_thenOk() throws Exception {
        RestAssured.given()
            .header("BAEL-username", "customUsername")
            .header("BAEL-authorities", "SCOPE_read")
            .get(PROJECT_RESOURCE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .body("size()", greaterThan(0));
    }

    @Test
    public void givenRequestWithPreAuthHeaders_whenPostProject_thenCreated() throws Exception {
        String newProject = "{ \"name\": \"newProject\" }";

        RestAssured.given()
            .header("BAEL-username", "customUsername")
            .header("BAEL-authorities", "SCOPE_write")
            .contentType(ContentType.JSON)
            .body(newProject)
            .post(PROJECT_RESOURCE_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void givenRequestWithInvalidAuthoritiesPreAuthHeader_whenPostProject_thenForbidden() throws Exception {
        String newProject = "{ \"name\": \"newProject\" }";

        RestAssured.given()
            .header("BAEL-username", "customUsername")
            .header("BAEL-authorities", "SCOPE_read")
            .contentType(ContentType.JSON)
            .body(newProject)
            .post(PROJECT_RESOURCE_URL)
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenJustUsernameHeaders_whenRequestProjectsEndpoint_thenForbidden() throws Exception {
        RestAssured.given()
            .header("BAEL-username", "customUsername")
            .get(PROJECT_RESOURCE_URL)
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenJustUsernameHeaders_whenRequestNonExistingEndpoint_thenNotFound() throws Exception {
        RestAssured.given()
            .header("BAEL-username", "customUsername")
            .get(PROJECT_RESOURCE_SERVER_BASE_URL + "/other")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenNoHeaders_whenRequestProjectsEndpoint_thenPreAuthCredentialsNotFoundException() throws Exception {
        RestAssured.given()
            .get(PROJECT_RESOURCE_URL)
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
