package com.baeldung.lss.test;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.baeldung.lss.spring.LssApp4;
import com.baeldung.lss.web.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LssApp4.class })
public class Lss4SecurityTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient restClient;

    @Before
    public void setup() {
        // @formatter:off
            this.restClient = WebTestClient
                    .bindToApplicationContext(this.context)                            
                    .apply(springSecurity()) 
                    .configureClient()                   
                    .filter(basicAuthentication())                      
                    //.filter(basicAuthentication("user","pass")) 
                    .build();
         // @formatter:on
    }

    //

    @Test
    public void givenWithoutAuthentication_whenTheUsersCollectionEndpointIsHit_then401UnAthorized() throws Exception {
        // @formatter:off
        this.restClient
                .get()
                .uri("/user/")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
        // @formatter:on
    }

    @Test
    // default role is USER
    @WithMockUser(username = "user")
    public void givenAuthenticatedWithTheUSERRole_whenTheDeleteEndpointIsHit_then403Forbidden() throws Exception {
        // @formatter:off
            this.restClient
                    .delete()
                    .uri("/user/delete/1")
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.FORBIDDEN); 
         // @formatter:on
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void givenAuthenticatedWithTheADMINRole_whenTheDeleteEndpointIsHit_then403Forbidden() throws Exception {
        // @formatter:off
            this.restClient
                    .delete()
                    .uri("/user/delete/1")
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.NO_CONTENT); 
         // @formatter:on
    }

    @Test
    public void givenAuthenticatedTheADMINRole_whenTheCreateEndpointIsHit_then201Created() throws Exception {
        // @formatter:off                        
        this.restClient
                .mutateWith(mockUser().roles("ADMIN"))
                .post()
                .uri("/user/")
                .syncBody(new User())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(User.class); 
        // @formatter:on
    }

}
