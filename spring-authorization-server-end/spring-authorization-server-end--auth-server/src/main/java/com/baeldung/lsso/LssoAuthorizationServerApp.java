package com.baeldung.lsso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;


@SpringBootApplication()
public class LssoAuthorizationServerApp {

    private static final Logger LOG = LoggerFactory.getLogger(LssoAuthorizationServerApp.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LssoAuthorizationServerApp.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(ServerProperties serverProperties) {

        return (evt) -> {

            Integer port = serverProperties.getPort();

            LOG.info("Spring Authorization Server started on: http://localhost:{}", port);
        };
    }

}
