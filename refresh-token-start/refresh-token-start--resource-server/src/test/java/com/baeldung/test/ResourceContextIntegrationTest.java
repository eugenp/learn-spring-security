package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.baeldung.um.spring.ResourceServerApp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ResourceServerApp.class })
public class ResourceContextIntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }

}