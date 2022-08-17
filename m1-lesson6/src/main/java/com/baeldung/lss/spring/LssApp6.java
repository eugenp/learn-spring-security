package com.baeldung.lss.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.baeldung.lss.persistence.InMemoryUserRepository;
import com.baeldung.lss.persistence.UserRepository;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan("com.baeldung.lss.web")
public class LssApp6 {

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp6.class, LssSecurityConfig.class, LssWebMvcConfiguration.class }, args);
    }

}
