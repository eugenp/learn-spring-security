package com.baeldung.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.baeldung.lss.spring.LssApp4;

import jakarta.servlet.Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { LssApp4.class })
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
    public void testUserLoginSuccess() throws Exception {
        ResultActions resultActions = mockMvc.perform(formLogin("/doLogin").user("test@email.com")
            .password("pass"));
        resultActions.andExpect(authenticated());
    }

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
