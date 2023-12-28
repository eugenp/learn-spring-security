package com.baeldung.lsso;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class ProjectResourceServerIntegrationTest {

    private static final String PROJECT_SVC_ENDPOINT_URL = "/api/projects";

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenRequestWithPreAuthHeaders_whenRequestProjectsEndpoint_thenOk() throws Exception {
        this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL).header("BAEL-username", "customUsername")
                .header("BAEL-authorities", "SCOPE_read")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)));
    }

    @Test
    public void givenRequestWithPreAuthHeaders_whenPostProject_thenCreated() throws Exception {
        String newProject = "{ \"name\": \"newProject\" }";

        this.mvc.perform(post(PROJECT_SVC_ENDPOINT_URL).header("BAEL-username", "customUsername")
                .header("BAEL-authorities", "SCOPE_write")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newProject))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenRequestWithInvalidAuthoritiesPreAuthHeader_whenPostProject_thenForbidden() throws Exception {
        String newProject = "{ \"name\": \"newProject\" }";

        this.mvc.perform(post(PROJECT_SVC_ENDPOINT_URL).header("BAEL-username", "customUsername")
                .header("BAEL-authorities", "SCOPE_read")
                .accept(MediaType.APPLICATION_JSON)
                .content(newProject))
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenJustUsernameHeaders_whenRequestProjectsEndpoint_thenForbidden() throws Exception {
        this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL).header("BAEL-username", "customUsername")
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
            this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL).accept(MediaType.APPLICATION_JSON))
                .andReturn();
        });
    }
}
