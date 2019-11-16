package com.baeldung.um.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@ComponentScan("com.baeldung.um")
@EnableJpaRepositories("com.baeldung.um")
@EntityScan("com.baeldung.um.web.model")
public class LssApp2 {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp2.class, AuthorizationServerConfiguration.class, ResourceServerConfiguration.class, UmWebMvcConfiguration.class }, args);
    }

    public static class AuthenticationMananagerProvider extends WebSecurityConfigurerAdapter {

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }
}
