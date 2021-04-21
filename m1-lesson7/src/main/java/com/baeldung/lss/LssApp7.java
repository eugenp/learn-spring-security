package com.baeldung.lss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class LssApp7 {

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(LssApp7.class, args);
    }

}
