package com.baeldung.lss.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AsyncBean {
    private static final Logger log = LoggerFactory.getLogger(AsyncBean.class);

    @Async
    public void asyncCall() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Inside Async: " + auth);
    }

}
