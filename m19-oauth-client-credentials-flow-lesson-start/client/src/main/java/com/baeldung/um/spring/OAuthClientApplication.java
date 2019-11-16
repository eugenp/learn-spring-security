package com.baeldung.um.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("com.baeldung.um")
@EnableScheduling
@SpringBootApplication
public class OAuthClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthClientApplication.class, args);
    }
}
