package com.baeldung.um.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.baeldung.um")
@SpringBootApplication
public class OAuthClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthClientApplication.class, args);
    }
}
