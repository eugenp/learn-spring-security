package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Needs the following to be running:
 * - Authorization Server
 * - Gateway
 * - Project Resource Server
 * - Task Resource Server
 * - SPA Client
 */
@Import(LssoClientTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LssoClientUILiveTest {

    @Autowired
    WebDriver chromeDriver;

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";
    private static final String CLIENT_BASE_URL = "http://localhost:8082";
    private static final String CLIENT_START_PAGE = CLIENT_BASE_URL + "/lsso-client";
    private WebDriverWait standardWait = null;

    @BeforeEach
    public void setupWait() {
        this.standardWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(4));
    }

    @Test
    public void givenServerStarts_thenEnsurePageLoads() {
        chromeDriver.get(CLIENT_START_PAGE);
        String pageSource = chromeDriver.getPageSource();
        assertThat(pageSource).isNotNull();

        // Ensure login button is present
        WebDriverWait standardWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(4));
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-button")));
        WebElement button = chromeDriver.findElement(By.className("login-button"));
        assertThat(button).isNotNull();
        String buttonText = button.getText();
        assertThat(buttonText).isEqualToIgnoringCase("Log In");
    }

    @Test
    public void givenLoginButtonClicked_thenEnsureLoginPageAppears() {
        chromeDriver.get(CLIENT_START_PAGE);

        // Click login button
        WebDriverWait standardWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(4));
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-button")));
        WebElement button = chromeDriver.findElement(By.className("login-button"));
        button.click();

        String pageTitle = chromeDriver.getTitle();
        assertThat(pageTitle).isEqualToIgnoringCase("Log in to baeldung");
        WebElement usernameField = chromeDriver.findElement(By.id("username"));
        assertThat(usernameField).isNotNull();
    }

    @Test
    public void givenRightCredentials_thenEnsureWeLoginSuccessfully() {
        login();

        // Ensure we got the create project section
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("create-project")));
        WebElement createProjectInput = chromeDriver.findElement(By.className("create-project"));
        assertThat(createProjectInput).isNotNull();

        // Ensure we got the projects section
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("projects")));
        List<WebElement> nrProjects = chromeDriver.findElements(By.className("project-item"));
        assertThat(nrProjects.size()).isGreaterThan(2);

        // Ensure we got the tasks section
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tasks")));
        WebElement tasksContainer = chromeDriver.findElement(By.className("tasks-container"));
        assertThat(tasksContainer).isNotNull();
    }

    @Test
    public void givenRightCredentials_thenWeCanCreateProjects() {
        login();

        // Count original number of projects
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("project-item")));
        int originalNrProjects = chromeDriver.findElements(By.className("project-item"))
            .size();

        // Ensure we can now create a new project
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("create-project")));
        WebElement createProjectInput = chromeDriver.findElement(By.className("create-project"))
            .findElement(By.tagName("input"));
        createProjectInput.sendKeys("New project");

        WebElement createProjectButton = chromeDriver.findElement(By.className("create-project"))
            .findElement(By.tagName("button"));
        createProjectButton.click();

        // Verify that there is now one additional project
        chromeDriver.navigate()
            .refresh();
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("project-item")));
        int newNrProjects = chromeDriver.findElements(By.className("project-item"))
            .size();
        assertThat(newNrProjects).isEqualTo(originalNrProjects + 1);
    }

    @Test
    public void givenRightCredentials_thenEnsureViewTasksButtonWorking() {
        login();

        // Ensure View Tasks button clicked (for first Project)
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("project-name")));
        WebElement viewTasksButton = chromeDriver.findElement(By.className("view-tasks-button"));
        viewTasksButton.click();

        // Verify that there's more than two tasks
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("task-items")));
        List<WebElement> tasks = chromeDriver.findElements(By.className("task-item"));
        assertThat(tasks.size()).isGreaterThan(2);
    }

    private void login() {
        chromeDriver.get(CLIENT_START_PAGE);

        // Get name of first window loaded
        List<String> originalWindowHandles = new ArrayList<>(chromeDriver.getWindowHandles());
        String mainWindowName = originalWindowHandles.get(0);

        // Click login button
        WebDriverWait standardWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(4));
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-button")));

        WebElement button = chromeDriver.findElement(By.className("login-button"));
        button.click();

        WebElement usernameField = chromeDriver.findElement(By.id("username"));
        usernameField.sendKeys(USERNAME);

        WebElement passwordField = chromeDriver.findElement(By.id("password"));
        passwordField.sendKeys(PASSWORD);

        WebElement submitButton = chromeDriver.findElement(By.id("kc-login"));
        submitButton.click();

        // Switch back to main window
        chromeDriver.switchTo()
            .window(mainWindowName);
    }

}
