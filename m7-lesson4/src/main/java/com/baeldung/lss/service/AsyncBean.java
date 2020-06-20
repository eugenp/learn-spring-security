package com.baeldung.lss.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncBean {

    @Async
    public void asyncCall() {
        System.out.println();
    }

}
