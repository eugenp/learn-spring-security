package com.baeldung.lss.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.baeldung.lss")
@EntityScan("com.baeldung.lss.web.model")
@ComponentScan("com.baeldung.persistence")
public class LssPersistenceConfig {

    public LssPersistenceConfig() {
        super();
    }

    //

}