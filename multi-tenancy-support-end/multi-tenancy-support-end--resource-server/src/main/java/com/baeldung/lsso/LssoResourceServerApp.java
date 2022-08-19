package com.baeldung.lsso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.baeldung.lsso.spring.AuthServersConfig;

@EnableConfigurationProperties(AuthServersConfig.class)
@SpringBootApplication
public class LssoResourceServerApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LssoResourceServerApp.class, args);
    }

}
