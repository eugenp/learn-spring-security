package com.baeldung.lss.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;

import com.baeldung.lss.service.UserServiceInterface;

@WebListener
public class StartupListener implements jakarta.servlet.ServletContextListener {

    @EJB(mappedName = "userService")
    UserServiceInterface userService;

    public void contextInitialized(ServletContextEvent sce) {
        userService.createTestUsers();
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
