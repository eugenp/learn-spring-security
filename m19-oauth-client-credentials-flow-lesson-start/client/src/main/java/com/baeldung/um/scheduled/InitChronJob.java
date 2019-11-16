package com.baeldung.um.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InitChronJob {

    @Value("${baeldung.resource-uri}")
    private String resourceUri;

    Logger logger = LoggerFactory.getLogger(InitChronJob.class);

}
