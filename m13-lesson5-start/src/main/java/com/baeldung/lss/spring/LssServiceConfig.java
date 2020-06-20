package com.baeldung.lss.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.baeldung.service")
public class LssServiceConfig {

    public LssServiceConfig() {
        super();
    }

    //

}