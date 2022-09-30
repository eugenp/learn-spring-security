package com.baeldung.lsso;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.baeldung.lsso.spring.CustomAuthoritiesConverter;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceServerTestingSupportIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenJwt_whenListProjects_thenOk() throws Exception {
        this.mvc.perform(get("/api/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)))
                .andExpect(jsonPath("$..name", Matchers.hasItems("Project 1", "Project 2")));
    }

    @Test
    public void givenJwt_whenCreateProject_thenOk() throws Exception {
        // Create project
        this.mvc.perform(post("/api/projects").with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Testing Project 1\"}"))
                .andExpect(status().isCreated());

        // Check projects
        this.mvc.perform(get("/api/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.greaterThan(3)))
                .andExpect(jsonPath("$..name", Matchers.hasItems("New Testing Project 1")));
    }

    @Test
    public void givenJwtWithDefaultScope_whenCreateProject_thenForbidden() throws Exception {
        this.mvc.perform(post("/api/projects").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Testing Project 1\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenJwtWithCustomAuthority_whenCreateProject_thenOk() throws Exception {
        // Create project
        this.mvc.perform(post("/api/projects").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Testing Project 1\"}"))
                .andExpect(status().isCreated());

        // Check projects
        this.mvc.perform(get("/api/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.greaterThan(3)))
                .andExpect(jsonPath("$..name", Matchers.hasItems("New Testing Project 1")));
    }

    @Test
    public void givenJwtWithConverter_whenListProjects_thenOk() throws Exception {
        this.mvc.perform(get("/api/projects").with(jwt().authorities(new CustomAuthoritiesConverter())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)))
                .andExpect(jsonPath("$..name", Matchers.hasItems("Project 1", "Project 2")));
    }

    @Test
    public void givenJwtWithConverter_whenCreateProject_thenOk() throws Exception {
        // Create project
        this.mvc.perform(post("/api/projects").with(jwt()
                                .jwt(jwtBuilder -> jwtBuilder.subject("admin"))
                                .authorities(new CustomAuthoritiesConverter()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Testing Project 1\"}"))
                .andExpect(status().isCreated());

        // Check projects
        this.mvc.perform(get("/api/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.greaterThan(3)))
                .andExpect(jsonPath("$..name", Matchers.hasItems("New Testing Project 1")));
    }

    @Test
    public void givenJwtWithExpiredToken_whenListProjects_thenOk() throws Exception {
        this.mvc.perform(get("/api/projects").with(jwt().jwt(jwtBuilderConsumer -> jwtBuilderConsumer
                        .expiresAt(Instant.now().minus(Duration.ofDays(5)))
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)))
                .andExpect(jsonPath("$..name", Matchers.hasItems("Project 1", "Project 2")));
    }
}
