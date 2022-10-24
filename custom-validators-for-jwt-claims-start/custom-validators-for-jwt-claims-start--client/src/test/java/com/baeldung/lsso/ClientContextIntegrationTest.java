package com.baeldung.lsso;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.baeldung.lsso.LssoClientApplication;

@SpringBootTest(classes = { LssoClientApplication.class })
public class ClientContextIntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }

}