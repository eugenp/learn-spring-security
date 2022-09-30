package com.baeldung.lsso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Note that the LssoAuthorizationServerApp needs to be running before starting the client application
 */
@SpringBootApplication
public class LssoClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(LssoClientApplication.class, args);
    }
}
