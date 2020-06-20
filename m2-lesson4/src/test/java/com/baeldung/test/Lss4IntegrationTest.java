package com.baeldung.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.baeldung.lss.spring.LssApp4;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LssApp4.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Lss4IntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .addFilters(springSecurityFilterChain)
            .build();
    }

    @Test
    public void whenLoadApplication_thenSuccess() {

    }

    @Test
    public void testUserLoginSuccess() throws Exception {
        ResultActions resultActions = mockMvc.perform(formLogin("/doLogin").user("test@email.com")
            .password("pass"));
        resultActions.andExpect(authenticated());
    }
}
