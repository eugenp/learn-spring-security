package com.baeldung.lss.test.integration.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Random;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.User;
import com.baeldung.lss.model.VerificationToken;
import com.baeldung.lss.test.integration.AbstractBaseIntegrationTest;
import com.baeldung.lss.validation.EmailExistsException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;


public class UserServiceIntegrationTest extends AbstractBaseIntegrationTest {

    @Override
    protected Boolean startSmtpServer() {
        return false;
    }

    // Tests

    // registerNewUser

    @Test
    public void whenRegisteringNewUser_thenUserRegistered() throws EmailExistsException {
        final User registeredUser = registerNewUser();

        final User retrievedUser = userService.findUserByEmail(VALUE_DEFAULT_USER_EMAIL);
        assertThat(retrievedUser.getId(), notNullValue());
        assertThat(retrievedUser.getId(), equalTo(registeredUser.getId()));
        assertThat(retrievedUser.getCreated(), notNullValue());
        assertThat(retrievedUser.getCreated(), equalTo(registeredUser.getCreated()));
        assertThat(retrievedUser.getEmail(), equalTo(VALUE_DEFAULT_USER_EMAIL));
        assertThat(retrievedUser.getEnabled(), equalTo(false));
        assertThat(retrievedUser.getPassword(), equalTo(VALUE_DEFAULT_USER_PASSWORD));
        assertThat(retrievedUser.getPasswordConfirmation(), nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRegisteringNullUser_thenException() throws EmailExistsException {
        userService.registerNewUser(null);
    }

    @Test(expected = EmailExistsException.class)
    public void givenRegisteredUser_whenRegisteringUserWithSameEmail_thenException() throws EmailExistsException {
        final User existingUser = registerNewUser();

        registerNewUser(existingUser.getEmail());
    }


    // findUserByEmail

    @Test
    public void whenRetrievingUserByNotExistingEmail_thenNullReturned() {
        final User notExistingUser = userService.findUserByEmail(RandomStringUtils.random(10));

        assertThat(notExistingUser, nullValue());
    }

    @Test
    public void givenTwoRegisteredUsers_whenRetrievingUserByEmail_thenCorrectUserReturned() throws EmailExistsException {
        registerNewUser();
        registerNewUser("integration.test2@email.com");
        final User foundUser = userService.findUserByEmail("integration.test2@email.com");

        assertThat(foundUser.getEmail(), equalTo("integration.test2@email.com"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRetrievingUserByNullEmail_thenException() {
        userService.findUserByEmail(null);
    }

    // createPasswordResetTokenForUser

    @Test
    public void givenExistingUser_whenCreatingPasswordResetToken_thenCreated() throws EmailExistsException {
        final User existingUser = registerNewUser();

        final String token = RandomStringUtils.random(10);
        userService.createPasswordResetTokenForUser(existingUser, token);

        final PasswordResetToken retrievedPasswordResetToken = userService.getPasswordResetToken(token);
        assertThat(retrievedPasswordResetToken.getToken(), equalTo(token));
        assertThat(retrievedPasswordResetToken.getUser().getId(), equalTo(existingUser.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreatingPasswordResetTokenForNotExistingUser_thenException() {
        final User notExistingUser = createNewUser();
        userService.createPasswordResetTokenForUser(notExistingUser, RandomStringUtils.random(10));
    }

    @Test
    public void givenUserExists_whenCreatingPasswordResetToken_thenPasswordResetTokenCreated() throws EmailExistsException {
        final User existingUser = registerNewUser();
        final String token = RandomStringUtils.random(10);
        userService.createPasswordResetTokenForUser(existingUser, token);

        final PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);
        assertThat(passwordResetToken.getToken(), equalTo(token));
        assertThat(passwordResetToken.getExpiryDate(), notNullValue());
        assertThat(passwordResetToken.getUser().getId(), equalTo(existingUser.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreatingPasswordResetTokenForNullUser_thenException() {
        userService.createPasswordResetTokenForUser(null, RandomStringUtils.random(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreatingPasswordResetTokenWithNullToken_thenException() throws EmailExistsException {
        userService.createPasswordResetTokenForUser(registerNewUser(), null);
    }

    // getPasswordResetToken

    @Test
    public void whenRetrievingNonExistingPasswordResetToken_thenNullReturned() {
        final String token = RandomStringUtils.random(10);
        final PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);

        assertThat(passwordResetToken, nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRetrievingPasswordResetTokenByNullToken_thenException() {
        userService.getPasswordResetToken(null);
    }

    // changeUserPassword

    @Test
    public void givenExistingUser_whenChangingPassword_thenPasswordChanged() throws EmailExistsException {
        final User user = registerNewUser();

        userService.changeUserPassword(user, "Aa1~aaaa");

        final User userWithUpdatedPassword = userService.findUserByEmail(user.getEmail());
        assertThat(userWithUpdatedPassword.getPassword(), equalTo("Aa1~aaaa"));
        assertThat(userWithUpdatedPassword.getPasswordConfirmation(), nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenChangingPasswordOfNotExistingUser_thenException() {
        final User user = createNewUser();
        user.setId(new Random().nextLong());
        userService.changeUserPassword(user, "Aa1~aaaa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenChangingPasswordForNullUser_thenException() {
        userService.changeUserPassword(null, "Aa1~aaaa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenChangingUserPasswordToNull_thenException() throws EmailExistsException {
        userService.changeUserPassword(registerNewUser(), null);
    }

    // createVerificationTokenForUser

    @Test
    public void givenExistingUser_whenCreatingVerificationToken_thenVerificationTokenCreated() throws EmailExistsException {
        final User existingUser = registerNewUser();

        final String token = RandomStringUtils.random(10);
        userService.createVerificationTokenForUser(existingUser, token);

        final VerificationToken retrievedToken = userService.getVerificationToken(token);
        assertThat(retrievedToken, notNullValue());
        assertThat(retrievedToken.getToken(), equalTo(token));
        assertThat(retrievedToken.getExpiryDate(), notNullValue());
        assertThat(retrievedToken.getUser().getId(), equalTo(existingUser.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreatingVerificationTokenForNotExistingUser_thenException() {
        final User notExistingUser = createNewUser();
        notExistingUser.setId(new Random(System.currentTimeMillis()).nextLong());

        final String token = RandomStringUtils.random(10);
        userService.createVerificationTokenForUser(notExistingUser, token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreatingVerificationTokenForNullUser_thenException() {
        userService.createVerificationTokenForUser(null, RandomStringUtils.random(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreatingVerificationTokenWithNullToken_thenException() throws EmailExistsException {
        userService.createVerificationTokenForUser(registerNewUser(), null);
    }

    // getVerificationToken

    @Test
    public void givenExistingVerificationToken_whenRetrievingVerificationToken_thenTokenFound() throws EmailExistsException {
        final User user = registerNewUser();
        final String token = RandomStringUtils.random(10);
        userService.createVerificationTokenForUser(user, token);

        final VerificationToken retrievedVerificationToken = userService.getVerificationToken(token);
        assertThat(retrievedVerificationToken, notNullValue());
    }

    @Test
    public void whenRetrievingNotExistingVerificationToken_thenNullReturned() {
        final VerificationToken notExistingVerificationToken = userService.getVerificationToken(RandomStringUtils.random(10));

        assertThat(notExistingVerificationToken, nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRetrievingVerificationTokenByNullToken_thenException() {
        userService.getVerificationToken(null);
    }

    // saveRegisteredUser

    @Test
    public void givenExistingUser_whenSavingUpdatedUser_thenUserUpdated() throws EmailExistsException {
        final User existingUser = registerNewUser();

        final User changedUser = createNewUser("integration.test.updated@email.com");
        changedUser.setId(existingUser.getId());
        userService.saveRegisteredUser(changedUser);

        final User retrievedUser = userService.findUserByEmail("integration.test.updated@email.com");
        assertThat(retrievedUser.getId(), equalTo(existingUser.getId()));
        assertThat(retrievedUser.getCreated(), equalTo(existingUser.getCreated()));
        assertThat(retrievedUser.getEmail(), equalTo("integration.test.updated@email.com"));
        assertThat(retrievedUser.getPassword(), equalTo(VALUE_DEFAULT_USER_PASSWORD));
        assertThat(retrievedUser.getEnabled(), equalTo(false));
        assertThat(userService.findUserByEmail(existingUser.getEmail()), nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSavingUpdatedUserWithNotExistingId_thenException() throws EmailExistsException {
        final User user = createNewUser();
        user.setId(new Random().nextLong());
        userService.saveRegisteredUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSavingNullUser_thenException() {
        userService.saveRegisteredUser(null);
    }

}
