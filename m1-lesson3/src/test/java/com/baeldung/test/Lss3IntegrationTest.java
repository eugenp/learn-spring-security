package com.baeldung.test;

import com.baeldung.lss.spring.LssApp3;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LssApp3.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class Lss3IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
