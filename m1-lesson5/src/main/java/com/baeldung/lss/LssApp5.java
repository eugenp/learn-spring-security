package com.baeldung.lss;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.map.repository.config.EnableMapRepositories;

@SpringBootApplication
@EnableMapRepositories(mapType = ConcurrentHashMap.class)
public class LssApp5 {

	public static void main(String[] args) {
		SpringApplication.run(LssApp5.class, args);
	}

}
