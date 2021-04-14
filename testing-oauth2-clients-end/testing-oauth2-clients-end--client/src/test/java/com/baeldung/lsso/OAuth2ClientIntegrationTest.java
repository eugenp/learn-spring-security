package com.baeldung.lsso;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2ClientIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void givenClient_whenGetRegistration_thenSuccess() {
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId("custom");
        assertThat(clientRegistration.getRegistrationId()).isEqualTo("custom");
        assertThat(clientRegistration.getClientId()).isEqualTo("lssoClient");
        assertThat(clientRegistration.getClientSecret()).isEqualTo("lssoSecret");
        assertThat(clientRegistration.getAuthorizationGrantType()
            .getValue()).isEqualTo("authorization_code");
        assertThat(clientRegistration.getRedirectUri()).isEqualTo("http://localhost:8082/lsso-client/login/oauth2/code/custom");
        assertThat(clientRegistration.getScopes()).hasSize(2)
            .contains("read", "write");
        assertThat(clientRegistration.getProviderDetails()
            .getAuthorizationUri()).isEqualTo("http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth");
        assertThat(clientRegistration.getProviderDetails()
            .getTokenUri()).isEqualTo("http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token");
        assertThat(clientRegistration.getProviderDetails()
            .getUserInfoEndpoint()
            .getUri()).isEqualTo("http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/userinfo");
        assertThat(clientRegistration.getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName()).isEqualTo("preferred_username");
    }

    @Test
    public void givenSecuredEndpoint_whenCallingEndpoint_thenSuccess() throws Exception {
        this.mvc.perform(get("/profile-simple"))
            .andExpect(status().is3xxRedirection());

        this.mvc.perform(get("/profile-simple").with(oauth2Client("custom")))
            .andExpect(status().isOk());
    }

    @Test
    public void givenOauth2Client_whenUsingScopes_thenSuccess() throws Exception {
        this.mvc.perform(get("/profile").with(oauth2Client("custom").accessToken(new OAuth2AccessToken(BEARER, "token", null, Instant.now(), Collections.singleton("admin.users:read")))))
            .andExpect(content().string("All users"))
            .andExpect(status().isOk());

        this.mvc.perform(get("/profile").with(oauth2Client("custom").accessToken(new OAuth2AccessToken(BEARER, "token", null, Instant.now(), Collections.singleton("users:read")))))
            .andExpect(content().string("Your user profile"))
            .andExpect(status().isOk());

        this.mvc.perform(get("/profile").with(oauth2Client("custom")))
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenOauth2Client_whenSetPrincipalName_thenSuccess() throws Exception {
        this.mvc.perform(get("/principal-name").with(oauth2Client("custom").principalName("admin@baeldung.com")))
            .andExpect(content().string("Welcome admin"))
            .andExpect(status().isOk());

        this.mvc.perform(get("/principal-name").with(oauth2Client("custom").principalName("user@gmail.com")))
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenRealClient_whenCallingEndpoint_thenSuccess() throws Exception {
        this.mvc.perform(get("/profile-simple").with(oauth2Client().clientRegistration(this.clientRegistrationRepository.findByRegistrationId("custom"))))
            .andExpect(status().isOk());
    }
}
