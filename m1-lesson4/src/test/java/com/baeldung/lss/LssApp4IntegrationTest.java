package com.baeldung.lss;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = LssApp4.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class LssApp4IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
