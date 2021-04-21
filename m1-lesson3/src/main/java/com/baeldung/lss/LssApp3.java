package com.baeldung.lss;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.map.repository.config.EnableMapRepositories;

@SpringBootApplication
@EnableMapRepositories(mapType = ConcurrentHashMap.class)
public class LssApp3 {

    public static void main(String[] args) {
        //SpringApplication.run(new Class[] { LssApp3.class, LssSecurityConfig.class }, args);
    	SpringApplication.run(LssApp3.class, args);
    }

}
