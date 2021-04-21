package com.baeldung.lss;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = LssApp3.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class LssApp3IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
