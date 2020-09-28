package com.baeldung.lss.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.baeldung.lss.spring.LssApp4;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LssApp4.class })
public class Lss4SecurityTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}
