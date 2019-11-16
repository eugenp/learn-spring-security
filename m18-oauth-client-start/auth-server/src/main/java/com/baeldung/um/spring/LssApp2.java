package com.baeldung.um.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@EnableAuthorizationServer
@SpringBootApplication
@ComponentScan("com.baeldung.um")
public class LssApp2 {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp2.class, UmSecurityConfig.class }, args);
    }

}
