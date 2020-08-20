package com.baeldung.lsso;

import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;

/**
 * This Live Test requires:
 * - the Task Resource Server to be running
 */
public class TaskResourceServerLiveTest {

    private static final String TASK_RESOURCE_SERVER_BASE_URL = "http://localhost:8085/lsso-task-resource-server";

    private static final String TASK_RESOURCE_URL = TASK_RESOURCE_SERVER_BASE_URL + "/api/tasks";

    @Test
    public void whenGetTaskResource_thenSuccess() throws Exception {
        RestAssured.given()
            .get(TASK_RESOURCE_URL + "?projectId=1")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .body("size()", greaterThan(0));
    }
}
