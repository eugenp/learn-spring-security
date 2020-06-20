package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.baeldung.lss.spring.ServletInitializer;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ServletInitializer.class })
@WebAppConfiguration
public class Lss6IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
