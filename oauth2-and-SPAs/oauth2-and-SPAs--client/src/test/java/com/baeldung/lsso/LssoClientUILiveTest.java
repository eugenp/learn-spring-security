package com.baeldung.lsso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-container")));
        WebElement button = chromeDriver.findElement(By.className("login-container")).findElement(By.tagName("button"));
        assertThat(button).isNotNull();
        String buttonText = button.getText();
        assertThat(buttonText).isEqualToIgnoringCase("login");
    }

    @Test
    public void givenLoginButtonClicked_thenEnsureLoginModalAppears() {

        chromeDriver.get(CLIENT_START_PAGE);

        // Get name of first window loaded
        List<String> originalWindowHandles = new ArrayList<>(chromeDriver.getWindowHandles());
        String mainWindowName = originalWindowHandles.get(0);

        // Click login button
        WebDriverWait standardWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(4));
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-container")));
        WebElement button = chromeDriver.findElement(By.className("login-container")).findElement(By.tagName("button"));
        button.click();

        String newWindowName = getNameOfLoginModal(mainWindowName);

        // Ensure we can switch to new window with login form and that the username field is present
        chromeDriver.switchTo().window(newWindowName);
        WebElement usernameField = chromeDriver.findElement(By.id("username"));
        assertThat(usernameField).isNotNull();

    }

    @Test
    public void givenRightCredentials_thenEnsureWeLoginSuccessfully() {
        login(Privileges.READ_AND_WRITE);

        // Ensure we got the welcome greeting
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("greeting")));
        WebElement welcomeGreeting = chromeDriver.findElement(By.className("greeting"));
        assertThat(welcomeGreeting.getText()).containsIgnoringCase("welcome");

        // Ensure we have a list of projects of at least size 2
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("projects")));
        List<WebElement> nrProjects = chromeDriver.findElements(By.className("project-item"));
        assertThat(nrProjects.size()).isGreaterThan(2);

        // Ensure we have "projects" and "profile" in top menu
        WebElement topMenu = chromeDriver.findElement(By.className("menu"));
        List<WebElement> menuItems = topMenu.findElements(By.tagName("li"));
        assertThat(menuItems.get(0).getText()).isEqualToIgnoringCase("projects");
        assertThat(menuItems.get(1).getText()).isEqualToIgnoringCase("profile");
    }

    @Test
    public void givenWrongCredentials_thenLoginShouldFail() {
        chromeDriver.get(CLIENT_START_PAGE);

        // Get name of first window loaded
        List<String> originalWindowHandles = new ArrayList<>(chromeDriver.getWindowHandles());
        String mainWindowName = originalWindowHandles.get(0);

        // Click login button
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-container")));
        WebElement button = chromeDriver.findElement(By.className("login-container")).findElement(By.tagName("button"));
        button.click();

        // Get name of new window for login modal
        String newWindowName = getNameOfLoginModal(mainWindowName);

        // Fill out form and attempt login with incorrect credentials
        chromeDriver.switchTo().window(newWindowName);
        WebElement usernameField = chromeDriver.findElement(By.id("username"));
        usernameField.sendKeys("wrong");

        WebElement passwordField = chromeDriver.findElement(By.id("password"));
        passwordField.sendKeys("credentials");

        WebElement submitButton = chromeDriver.findElement(By.id("kc-login"));
        submitButton.click();

        // Now modal should still be open and an error message displayed
        WebElement errorDiv = chromeDriver.findElement(By.className("alert-error"));
        assertThat(errorDiv).isNotNull();

        WebElement errorText = errorDiv.findElement(By.className("kc-feedback-text"));
        assertThat(errorText.getText()).isEqualToIgnoringCase("invalid username or password.");
    }

    @Test
    public void givenWritePrivileges_thenWeCanCreateProjects() {

        login(Privileges.READ_AND_WRITE);
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("create-project")));

        // Count original number of projects
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("projects")));
        int originalNrProjects = chromeDriver.findElements(By.className("project-item")).size();

        // Ensure we can now create a new project
        WebElement createProjectInput = chromeDriver.findElement(By.className("create-project")).findElement(By.tagName("input"));
        createProjectInput.sendKeys("New project");

        WebElement createProjectButton = chromeDriver.findElement(By.className("create-project")).findElement(By.tagName("button"));
        createProjectButton.click();

        // Reload page and hit login button again to fetch updated source
        chromeDriver.navigate().refresh();

        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-container")));
        WebElement button = chromeDriver.findElement(By.className("login-container")).findElement(By.tagName("button"));
        button.click();

        // Verify that there is now one additional project
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("projects")));
        int newNrProjects = chromeDriver.findElements(By.className("project-item")).size();
        assertThat(newNrProjects).isEqualTo(originalNrProjects + 1);
    }

    @Test
    public void givenReadPrivileges_thenWeCannotCreateProjects() {
        login(Privileges.READ_ONLY);

        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("create-project")));

        // Try to create project
        WebElement createProjectInput = chromeDriver.findElement(By.className("create-project")).findElement(By.tagName("input"));
        createProjectInput.sendKeys("New project");

        WebElement createProjectButton = chromeDriver.findElement(By.className("create-project")).findElement(By.tagName("button"));
        createProjectButton.click();

        // Locate error alert
        standardWait.until(ExpectedConditions.alertIsPresent());
        Alert errorAlert = chromeDriver.switchTo().alert();
        assertThat(errorAlert).isNotNull();
        assertThat(errorAlert.getText()).containsIgnoringCase("403");
    }

    private void login(Privileges accessType) {
        chromeDriver.get(CLIENT_START_PAGE);
        // Get name of first window loaded
        List<String> originalWindowHandles = new ArrayList<>(chromeDriver.getWindowHandles());
        String mainWindowName = originalWindowHandles.get(0);

        // Click login button
        WebDriverWait standardWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(4));
        standardWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-container")));

        if (accessType == Privileges.READ_ONLY) {
            WebElement writeCheckbox = chromeDriver.findElement(By.id("writeCb"));
            writeCheckbox.click();
        }
        WebElement button = chromeDriver.findElement(By.className("login-container")).findElement(By.tagName("button"));
        button.click();

        // Get name of new window for login modal
        String newWindowName = getNameOfLoginModal(mainWindowName);

        // Fill out form and attempt login
        chromeDriver.switchTo().window(newWindowName);
        WebElement usernameField = chromeDriver.findElement(By.id("username"));
        usernameField.sendKeys(USERNAME);

        WebElement passwordField = chromeDriver.findElement(By.id("password"));
        passwordField.sendKeys(PASSWORD);

        WebElement submitButton = chromeDriver.findElement(By.id("kc-login"));
        submitButton.click();

        // Switch back to main window
        chromeDriver.switchTo().window(mainWindowName);
    }

    private String getNameOfLoginModal(String nameOfMainWindow) {
        // Get name of new window for login modal
        List<String> windowHandles = new ArrayList<>(chromeDriver.getWindowHandles());
        for (String windowName : windowHandles) {
            if (windowName.equals(nameOfMainWindow)) {
                continue;
            }
            return windowName;
        }
        return "";
    }

    private enum Privileges {
        READ_ONLY, READ_AND_WRITE
    }
}
