package com.baeldung.um.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.baeldung.um")
public class ResourceServerApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { ResourceServerApp.class, SecurityConfig.class, }, args);
    }

}
