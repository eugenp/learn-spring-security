package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baeldung.lss.spring.LssApp5;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(LssApp5.class)
@IntegrationTest
public class Lss5IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}