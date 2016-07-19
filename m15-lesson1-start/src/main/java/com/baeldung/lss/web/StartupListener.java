package com.baeldung.lss.web;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import com.baeldung.lss.service.UserServiceInterface;

@WebListener
public class StartupListener implements javax.servlet.ServletContextListener {

    @EJB(mappedName = "userService")
    UserServiceInterface userService;

    public void contextInitialized(ServletContextEvent sce) {
        userService.createTestUsers();
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
