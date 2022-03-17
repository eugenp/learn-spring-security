package com.baeldung.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.baeldung.lss.spring.LssApp;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LssApp.class})
public class LssAppIntegrationTest {

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
        ResultActions resultActions = mockMvc.perform(formLogin("/doLogin").user("user@email.com")
                .password("pass"));

        resultActions.andExpect(authenticated());
    }

    @Test
    public void testAdminLoginSuccess() throws Exception {
        ResultActions resultActions = mockMvc.perform(formLogin("/doLogin").user("admin@email.com")
                .password("admin"));

        resultActions.andExpect(authenticated());
    }

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
