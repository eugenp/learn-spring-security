package com.baeldung.lss.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.baeldung.lss.persistence.InMemoryUserRepository;
import com.baeldung.lss.persistence.UserRepository;

@SpringBootApplication
@ComponentScan("com.baeldung.lss.web")
public class LssApp6 { 
    
    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp6.class, LssSecurityConfig.class, LssWebMvcConfiguration.class }, args);
    }

}
