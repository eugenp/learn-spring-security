package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.baeldung.lss.spring.LssApp3;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= LssApp3.class)
public class Lss3IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}