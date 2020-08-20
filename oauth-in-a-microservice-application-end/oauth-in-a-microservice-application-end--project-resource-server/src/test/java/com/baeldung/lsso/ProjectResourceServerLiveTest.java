package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.baeldung.lsso.web.dto.ProjectDto;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * This Live Test requires:
 * - the Project Resource Server to be running
 *
 */
public class ProjectResourceServerLiveTest {

    private static final String PROJECT_RESOURCE_SERVER_BASE_URL = "http://localhost:8081";

    private static final String PROJET_RESOURCE_URL = PROJECT_RESOURCE_SERVER_BASE_URL + "/lsso-project-resource-server/api/projects";

    @SuppressWarnings("unchecked")
    @Test
    public void whenGetProjectResource_thenSuccess() {
        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .get(PROJET_RESOURCE_URL);
        System.out.println(response.asString());
        assertThat(response.as(List.class)).hasSizeGreaterThan(0);
    }

    @Test
    public void whenPostNewProjectResource_thenCreated() {
        ProjectDto newProject = new ProjectDto(null, "newProject", LocalDate.now());

        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(newProject)
            .log()
            .all()
            .post(PROJET_RESOURCE_URL);
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void whenPutNewProjectResource_thenCreated() {
        ProjectDto newProject = new ProjectDto(null, "newProject", LocalDate.now());

        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(newProject)
            .log()
            .all()
            .put(PROJET_RESOURCE_URL + "/1");
        System.out.println(response.asString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

}
