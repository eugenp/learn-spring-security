package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.baeldung.lss.spring.LssApp2;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LssApp2.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Lss2IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
