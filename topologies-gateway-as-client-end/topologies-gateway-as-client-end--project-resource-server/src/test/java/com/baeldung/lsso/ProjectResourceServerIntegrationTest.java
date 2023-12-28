package com.baeldung.lsso;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProjectResourceServerIntegrationTest {

    private static final String PROJECT_SVC_ENDPOINT_URL = "/api/projects";

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenJwt_whenGetProjectsEndpoint_thenOk() throws Exception {
        this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL).with(jwt())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)));
    }

    @Test
    public void notGivenJwt_whenGetProjectsEndpoint_thenUnauthorized() throws Exception {
        this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    @Test
    public void givenJwtAndOnlyWriteScope_whenGetProjectsEndpoint_thenForbidden() throws Exception {
        this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL).with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "write")))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE,
                allOf(containsString("insufficient_scope"), containsString("The request requires higher privileges than provided by the access token"))));
    }

    @Test
    public void givenJwtAndReadScope_whenGetProjectsEndpoint_thenOk() throws Exception {
        this.mvc.perform(get(PROJECT_SVC_ENDPOINT_URL).with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "read")))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)));
    }

    @Test
    public void givenJwtAndWriteScope_whenPostProjectsEndpoint_thenOk() throws Exception {
        this.mvc.perform(post(PROJECT_SVC_ENDPOINT_URL).with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "write")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Project New\",\"dateCreated\":\"2023-01-01\"}"))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenJwtAndOnlyReadScope_whenPostProjectsEndpoint_thenForbidden() throws Exception {
        this.mvc.perform(post(PROJECT_SVC_ENDPOINT_URL).with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "read")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Project New\",\"dateCreated\":\"2023-01-01\"}"))
            .andExpect(status().isForbidden())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE,
                allOf(containsString("insufficient_scope"), containsString("The request requires higher privileges than provided by the access token"))));
    }

}
