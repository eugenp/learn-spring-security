package com.baeldung.lsso;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestConfiguration
public class LssoClientTestConfig {

    @Bean
    public WebDriver chromeDriver() {
        WebDriverManager.chromedriver()
            .setup();
        return new ChromeDriver();
    }
}
