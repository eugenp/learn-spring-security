package com.baeldung.lsso;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { LssoAuthorizationServerApp.class })
public class AuthContextIntegrationTest {

    @Test
    public void whenLoadApplication_thenSuccess() {

    }

}