package com.baeldung.lsso;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskResourceServerIntegrationTest {

    private static final String TASKS_SVC_ENDPOINT_URL = "/api/tasks";

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenRequestWithPreAuthHeaders_whenRequestProjectsEndpoint_thenOk() throws Exception {
        this.mvc.perform(get(TASKS_SVC_ENDPOINT_URL + "?projectId=1").header("BAEL-username", "customUsername")
                .header("BAEL-authorities", "SCOPE_read")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)));
    }

    @Test
    public void givenRequestWithNoRequiredQueryParam_whenRequestProjectsEndpoint_thenBadRequest() throws Exception {
        this.mvc.perform(get(TASKS_SVC_ENDPOINT_URL).header("BAEL-username", "customUsername")
                .header("BAEL-authorities", "SCOPE_read")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenJustUsernameHeaders_whenRequestProjectsEndpoint_thenForbidden() throws Exception {
        this.mvc.perform(get(TASKS_SVC_ENDPOINT_URL).header("BAEL-username", "customUsername")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenJustUsernameHeaders_whenRequestNonExistingEndpoint_thenNotFound() throws Exception {
        this.mvc.perform(get("/other").header("BAEL-username", "customUsername")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoHeaders_whenRequestProjectsEndpoint_thenPreAuthCredentialsNotFoundException() throws Exception {
        assertThrows(PreAuthenticatedCredentialsNotFoundException.class, () -> {
            this.mvc.perform(get(TASKS_SVC_ENDPOINT_URL).accept(MediaType.APPLICATION_JSON))
                .andReturn();
        });
    }
}
