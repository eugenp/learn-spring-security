package com.baeldung.test;

import static org.hamcrest.Matchers.isA;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.baeldung.um.spring.LssApp2;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LssApp2.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthorizationServerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() throws Exception {

        this.mockMvc.perform(post("/oauth/token").with(httpBasic("lssClient", "lssSecret"))
            .param("grant_type", "client_credentials"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token", isA(String.class)));
    }

}
