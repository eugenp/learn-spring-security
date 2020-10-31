package com.baeldung.test;

import com.baeldung.lss.spring.LssApp2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(LssApp2.class)
@ActiveProfiles("test")
public class Lss2IntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }
}