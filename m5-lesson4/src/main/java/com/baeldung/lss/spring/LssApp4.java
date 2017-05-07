package com.baeldung.lss.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.baeldung.lss.web","com.baeldung.lss.spring"})
@EnableJpaRepositories("com.baeldung.lss")
@EntityScan("com.baeldung.lss.web.model")
public class LssApp4 extends SpringBootServletInitializer{

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp4.class, LssSecurityConfig.class, LssWebMvcConfiguration.class }, args);
    }

}
