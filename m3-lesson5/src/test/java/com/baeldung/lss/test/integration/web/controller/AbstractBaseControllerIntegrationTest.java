package com.baeldung.lss.test.integration.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.baeldung.lss.test.integration.AbstractBaseIntegrationTest;
import com.baeldung.lss.web.controller.RegistrationController;
import com.baeldung.lss.web.controller.UserController;

public abstract class AbstractBaseControllerIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    protected UserDetailsService userDetailsService;

    @Autowired
    private UserController userController;

    @Autowired
    private RegistrationController registrationController;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected MockMvc mockMvc;

    @Override
    public void setup() {
        super.setup();
        mockMvc = MockMvcBuilders.standaloneSetup(userController, registrationController)
            .build();
    }

}
