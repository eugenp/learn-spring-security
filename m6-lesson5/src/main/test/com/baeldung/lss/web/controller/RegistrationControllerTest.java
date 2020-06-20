package com.baeldung.lss.web.controller;

import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class RegistrationControllerTest {

    @Test
    public void measureRegistrationTime() throws IOException {
        final Long startTimestamp = System.currentTimeMillis();
        final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://localhost:8081/user/register").openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        httpURLConnection.setDoOutput(true);
        final String body = "id=&email=performance.test." + System.currentTimeMillis() + "%40email.com&password=Aa1%7Eaaaa&passwordConfirmation=Aa1%7Eaaaa";
        httpURLConnection.getOutputStream().write(body.getBytes());

        final Integer responseCode = httpURLConnection.getResponseCode();
        Assert.assertThat(responseCode, equalTo(200));

        final Long executionTime = System.currentTimeMillis() - startTimestamp;
        System.out.println("Execution time: " + executionTime);
    }
}
