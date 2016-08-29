package com.baeldung.um.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.baeldung.um")
@EnableJpaRepositories("com.baeldung.um")
@EntityScan("com.baeldung.um.web.model")
public class LssApp3 extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LssApp3.class);
    }
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp3.class, AuthorizationServerConfiguration.class, ResourceServerConfiguration.class, UmWebMvcConfiguration.class }, args);
    }

}
