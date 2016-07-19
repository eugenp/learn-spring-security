package com.baeldung.lss.spring.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.baeldung.lss.spring.security.LssSecurityConfig;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    public SecurityWebApplicationInitializer() {
        super(LssSecurityConfig.class);
    }

}
