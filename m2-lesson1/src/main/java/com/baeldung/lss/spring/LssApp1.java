package com.baeldung.lss.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.baeldung.lss")
@EnableJpaRepositories("com.baeldung.lss")
@EntityScan("com.baeldung.lss.web.model")
public class LssApp1 extends SpringBootServletInitializer {
	
	private final static Class<?>[] CONFIGS = { // @formatter:off
			LssSecurityConfig.class,
			LssWebMvcConfiguration.class,           
			LssApp1.class            
    }; // // @formatter:on

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CONFIGS);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CONFIGS,args);
	}

}
