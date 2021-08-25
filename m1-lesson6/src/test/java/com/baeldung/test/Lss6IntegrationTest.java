package com.baeldung.test;


import com.baeldung.lss.LssApp6;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = LssApp6.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Lss6IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
