package com.baeldung.lsso;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProjectResourceServerIntegrationTest {

    private static final String PROJECT_SVC_ENDPOINT_URL = "/api/projects";

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenGetRequest_whenProjectsEndpoint_thenOk() throws Exception {
        this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)));
    }

    @Test
    public void givenPostRequest_whenProjectsEndpoint_thenCreated() throws Exception {
        String newProject = "{ \"name\": \"newProject\" }";

        this.mvc.perform(post(PROJECT_SVC_ENDPOINT_URL).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newProject))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenPutRequest_whenProjectsEndpoint_thenCreated() throws Exception {
        String existingProject = "{ \"name\": \"existingProject\" }";

        this.mvc.perform(put(PROJECT_SVC_ENDPOINT_URL + "/1").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(existingProject))
            .andExpect(status().isOk());
    }

}
