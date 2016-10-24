package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baeldung.lss.spring.LssApp2;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(LssApp2.class)
@IntegrationTest
public class Lss2IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
