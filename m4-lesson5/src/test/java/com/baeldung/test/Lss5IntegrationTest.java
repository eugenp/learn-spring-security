package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baeldung.lss.spring.LssApp5;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { LssApp5.class })
public class Lss5IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}