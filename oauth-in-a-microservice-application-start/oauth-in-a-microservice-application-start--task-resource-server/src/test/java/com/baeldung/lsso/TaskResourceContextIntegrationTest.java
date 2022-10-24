package com.baeldung.lsso;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { LssoTaskResourceServerApp.class })
public class TaskResourceContextIntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }

}