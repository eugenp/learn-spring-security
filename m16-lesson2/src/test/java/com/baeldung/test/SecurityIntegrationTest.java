package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import com.baeldung.lss.spring.LssApp2;
import com.baeldung.lss.web.controller.UserController;

import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LssApp2.class, webEnvironment = WebEnvironment.NONE)
public class SecurityIntegrationTest {

    @Autowired
    private UserController userApi;

    @Test
    public void whenDeleting_thenAccessDenied() {
        StepVerifier.create(this.userApi.delete(1l))
            .expectError(AccessDeniedException.class)
            .verify();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void givenAdminUser_whenDeleting_thenSuccess() {
        StepVerifier.create(this.userApi.delete(1l))
            .verifyComplete();
    }
}
