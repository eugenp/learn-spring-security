package com.baeldung.um.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.baeldung.um.dto.Foo;

@Component
public class InitChronJob {

    @Value("${baeldung.resource-uri}")
    private String resourceUri;

    Logger logger = LoggerFactory.getLogger(InitChronJob.class);

    @Autowired
    private WebClient webClient;

    @Scheduled(fixedRate = 15000)
    public void automaticTask() {
        webClient.get()
            .uri(resourceUri)
            .retrieve()
            .bodyToMono(Foo.class)
            .subscribe(foo -> logger.info("Retrieved Foo {}", foo));
    }

}
