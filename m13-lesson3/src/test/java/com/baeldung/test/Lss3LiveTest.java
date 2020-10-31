package com.baeldung.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baeldung.lss.spring.LssApp3;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= LssApp3.class)
// requires a running Redis server on localhost port 6379 
public class Lss3LiveTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}