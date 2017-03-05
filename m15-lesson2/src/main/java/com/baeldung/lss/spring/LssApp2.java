package com.baeldung.lss.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.baeldung.lss")
@EnableJpaRepositories("com.baeldung.lss")
@EntityScan("com.baeldung.lss.model")
public class LssApp2 {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp2.class, LssSecurityConfig.class, LssWebMvcConfiguration.class }, args);
    }

}
