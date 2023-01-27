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

import com.baeldung.lsso.spring.CustomAuthoritiesExtractor;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceServerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void notGivenJwt_whenHttpGet_thenUnauthorized() throws Exception {
        this.mvc.perform(get("/api/projects"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    @Test
    public void givenJwtAndOnlyWriteScope_whenHttpGet_thenForbidden() throws Exception {
        this.mvc.perform(get("/api/projects").with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "write")))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, allOf(containsString("insufficient_scope"), containsString("The request requires higher privileges than provided by the access token"))));
    }

    @Test
    public void givenJwtAndWriteScope_whenHttpPost_thenOk() throws Exception {
        this.mvc.perform(post("/api/projects").with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "write")))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Project 1\",\"dateCreated\":\"2019-06-13\"}"))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenJwtAndOnlyReadScope_whenHttpPost_thenForbidden() throws Exception {
        this.mvc.perform(post("/api/projects").with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "read")))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\":1,\"name\":\"Project 1\",\"dateCreated\":\"2019-06-13\"}"))
            .andExpect(status().isForbidden())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, allOf(containsString("insufficient_scope"), containsString("The request requires higher privileges than provided by the access token"))));
    }

    @Test
    public void givenJwtAndReadScopeWithEmailFormatUsernameAsClaim_whenHttpGet_thenOk() throws Exception {
        this.mvc.perform(get("/api/projects").with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "read")
            .claim("preferred_username", "john@test.com"))
            .authorities(new CustomAuthoritiesExtractor()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", Matchers.greaterThan(0)));
    }

    @Test
    public void givenJwtAndReadScopeWithoutEmailFormatUsernameAsClaim_whenHttpGet_thenForbidden() throws Exception {
        this.mvc.perform(get("/api/projects").with(jwt().jwt(jwtBuilder -> jwtBuilder.claim("scope", "read")
            .claim("preferred_username", "john_test_username"))
            .authorities(new CustomAuthoritiesExtractor()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, allOf(containsString("insufficient_scope"), containsString("The request requires higher privileges than provided by the access token"))));

    }
}