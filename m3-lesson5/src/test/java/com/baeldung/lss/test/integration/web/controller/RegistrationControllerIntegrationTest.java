package com.baeldung.lss.test.integration.web.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.User;
import com.baeldung.lss.model.VerificationToken;
import com.baeldung.lss.validation.EmailExistsException;

public class RegistrationControllerIntegrationTest extends AbstractBaseControllerIntegrationTest {

    @Override
    protected Boolean startSmtpServer() {
        return true;
    }

    // Tests

    // registerUser: /user/register

    @Test
    public void whenRegisteringUser_thenUserIsCreatedAndVerificationEmailIsSent() throws Exception {
        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/register").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")));

        final User persistedUser = userService.findUserByEmail(VALUE_DEFAULT_USER_EMAIL);
        assertThat(persistedUser.getId(), notNullValue());
        assertThat(persistedUser.getCreated(), notNullValue());
        assertThat(persistedUser.getEnabled(), equalTo(false));
        assertTrue(passwordEncoder.matches(VALUE_DEFAULT_USER_PASSWORD, persistedUser.getPassword()));
        assertThat(persistedUser.getPasswordConfirmation(), nullValue());

        assertThat(getEmailsCount(), equalTo(1));
        final String emailContent = getEmail(0).getContent()
            .toString();
        final String token = emailContent.substring(emailContent.lastIndexOf("token=") + 6)
            .trim();
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        assertThat(verificationToken, notNullValue());
        assertThat(verificationToken.getUser()
            .getEmail(), equalTo(VALUE_DEFAULT_USER_EMAIL));
        assertThat(verificationToken.getExpiryDate(), notNullValue());
        assertThat(verificationToken.getToken(), equalTo(token));
    }

    @Test
    public void whenRegisteringUserWithNoEmailProvided_thenUserNotRegistered() throws Exception {
        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/register").param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("registrationPage")))
            .andExpect(model().attribute("user", notNullValue()))
            .andExpect(model().attributeHasFieldErrors("user", "email"));

        assertThatNoUsersRegistered();
    }

    @Test
    public void whenRegisteringUserWithNoPasswordProvided_thenUserNotRegistered() throws Exception {
        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/register").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("registrationPage")))
            .andExpect(model().attribute("user", notNullValue()))
            .andExpect(model().attributeHasFieldErrors("user", "password"));

        assertThatNoUsersRegistered();
    }

    @Test
    public void whenRegisteringUserWithNotMatchingPasswords_thenUserNotRegistered() throws Exception {
        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/register").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", "not matching password"))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("registrationPage")))
            .andExpect(model().attribute("user", notNullValue()))
            .andExpect(model().attributeHasFieldErrors("user"));

        assertThatNoUsersRegistered();
    }

    @Test
    public void whenRegisteringUserWithInvalidPassword_thenUseNotRegistered() throws Exception {
        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/register").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", "invalid password")
            .param("passwordConfirmation", "invalid password"))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("registrationPage")))
            .andExpect(model().attribute("user", notNullValue()))
            .andExpect(model().attributeHasFieldErrors("user", "password"));

        assertThatNoUsersRegistered();
    }

    @Test
    public void whenRegisteringUserWithInvalidEmail_thenUserNotRegistered() throws Exception {
        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/register").param("email", "not a email")
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("registrationPage")))
            .andExpect(model().attribute("user", notNullValue()))
            .andExpect(model().attributeHasFieldErrors("user", "email"));

        assertThatNoUsersRegistered();
    }

    @Test
    public void givenRegisteredUser_whenRegisteringUserWithEmailDuplicate_thenUserNotRegistered() throws Exception, EmailExistsException {
        registerNewUser();

        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/register").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("registrationPage")))
            .andExpect(model().attribute("user", notNullValue()))
            .andExpect(model().attributeHasFieldErrors("user", "email"));

        assertThat(getEmailsCount(), equalTo(0));
        assertThat(userRepository.count(), equalTo(1L));
    }

    // confirmRegistration: /registrationConfirm

    @Test
    public void givenVerificationTokenExist_whenConfirmingRegistration_thenUserBecameEnabled() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        final String token = RandomStringUtils.random(10);
        verificationTokenRepository.save(new VerificationToken(token, user));

        mockMvc.perform(post("/registrationConfirm").param("token", token))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")));

        final User persistedUser = userService.findUserByEmail(VALUE_DEFAULT_USER_EMAIL);
        assertThat(persistedUser, notNullValue());
        assertThat(persistedUser.getEnabled(), equalTo(true));
    }

    @Test
    public void whenConfirmingRegistrationWithNotExistingToken_thenErrorMessageIsShown() throws Exception {
        mockMvc.perform(post("/registrationConfirm").param("token", RandomStringUtils.random(10)))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    public void whenConfirmingRegistrationWithExpiredToken_thenErrorMessageIsShown() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        final String token = RandomStringUtils.random(10);
        final VerificationToken verificationToken = new VerificationToken(token, user);
        verificationToken.setExpiryDate(new Date(System.currentTimeMillis() - 1));
        verificationTokenRepository.save(verificationToken);

        mockMvc.perform(post("/registrationConfirm").param("token", token))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")))
            .andExpect(flash().attributeExists("errorMessage"));

        final User persistedUser = userService.findUserByEmail(VALUE_DEFAULT_USER_EMAIL);
        assertThat(persistedUser.getEnabled(), equalTo(false));
    }

    @Test
    public void whenConfirmingRegistrationWithNoTokenProvided_then400() throws Exception {
        mockMvc.perform(post("/registrationConfirm"))
            .andExpect(status().isBadRequest());
    }

    // resetPassword: /user/resetPassword

    @Test
    public void givenUserExists_whenRequestingPasswordReset_thenEmailSent() throws EmailExistsException, Exception {
        final User user = registerNewUser();

        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/resetPassword").param("email", VALUE_DEFAULT_USER_EMAIL))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")))
            .andExpect(flash().attributeExists("message"));

        assertThat(getEmailsCount(), equalTo(1));
        final String emailContent = getEmail(0).getContent()
            .toString();
        final String token = emailContent.substring(emailContent.lastIndexOf("token=") + 6)
            .trim();

        assertThat(passwordResetTokenRepository.count(), equalTo(1L));
        final PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);
        assertThat(passwordResetToken, notNullValue());
        assertThat(passwordResetToken.getUser()
            .getId(), equalTo(user.getId()));
        assertThat(passwordResetToken.getExpiryDate(), notNullValue());
        assertThat(passwordResetToken.getToken(), equalTo(token));
    }

    @Test
    public void whenRequestingPasswordReset_thenEmailNotSent() throws EmailExistsException, Exception {
        assertThat(getEmailsCount(), equalTo(0));
        mockMvc.perform(post("/user/resetPassword").param("email", "not.existing.email@email.com"))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")))
            .andExpect(flash().attributeExists("message"));

        assertThat(getEmailsCount(), equalTo(0));
        assertThat(passwordResetTokenRepository.count(), equalTo(0L));
    }

    @Test
    public void whenRequestingPasswordResetWithNoEmailProvided_then400() throws Exception {
        mockMvc.perform(post("/user/resetPassword"))
            .andExpect(status().isBadRequest());
    }

    // savePassword: /user/savePassword

    @Test
    public void givenUserAuthenticated_whenChangingPassword_thenPasswordChanged() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userDetailsService.loadUserByUsername(user.getEmail())
            .getAuthorities());
        try {
            SecurityContextHolder.getContext()
                .setAuthentication(auth);

            final String newPassword = VALUE_DEFAULT_USER_PASSWORD + "+";

            mockMvc.perform(post("/user/savePassword").param("password", newPassword)
                .param("passwordConfirmation", newPassword))
                .andExpect(status().isFound())
                .andExpect(view().name(equalTo("redirect:/login")))
                .andExpect(flash().attributeExists("message"));

            final User persistedUser = userService.findUserByEmail(VALUE_DEFAULT_USER_EMAIL);
            assertTrue(passwordEncoder.matches(newPassword, persistedUser.getPassword()));
            assertThat(persistedUser.getPasswordConfirmation(), nullValue());
        } finally {
            SecurityContextHolder.getContext()
                .setAuthentication(null);
        }
    }

    @Test
    public void givenPasswordsDontMatch_whenChangingPassword_thenErrorMessageIsShown() throws Exception, EmailExistsException {
        final User user = registerNewUser();
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userDetailsService.loadUserByUsername(user.getEmail())
            .getAuthorities());
        try {
            SecurityContextHolder.getContext()
                .setAuthentication(auth);

            mockMvc.perform(post("/user/savePassword").param("password", VALUE_DEFAULT_USER_PASSWORD + "+")
                .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD + "-"))
                .andExpect(status().isOk())
                .andExpect(view().name(equalTo("resetPassword")));
        } finally {
            SecurityContextHolder.getContext()
                .setAuthentication(null);
        }
    }

    // showChangePasswordPage: /user/changePassword

    @Test
    public void givenPasswordResetTokenExists_whenRequestingChangePasswordPage_thenPasswordResetPageViewReturned() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        final String token = RandomStringUtils.random(10);
        final PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);

        try {
            mockMvc.perform(get("/user/changePassword").param("id", user.getId()
                .toString())
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name(equalTo("resetPassword")));

            final User principal = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
            assertThat(principal.getId(), equalTo(user.getId()));
        } finally {
            SecurityContextHolder.getContext()
                .setAuthentication(null);
        }
    }

    @Test
    public void whenRequestingChangePasswordPageWithNoTokenProvided_then400() throws EmailExistsException, Exception {
        final User user = registerNewUser();

        mockMvc.perform(get("/user/changePassword").param("id", user.getId()
            .toString()))
            .andExpect(status().isBadRequest());
        assertThat(SecurityContextHolder.getContext()
            .getAuthentication(), nullValue());
    }

    @Test
    public void whenRequestingChangePasswordPageWithNoUserIdProvided_then400() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        final String token = RandomStringUtils.random(10);
        final PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);

        mockMvc.perform(get("/user/changePassword").param("token", token))
            .andExpect(status().isBadRequest());
        assertThat(SecurityContextHolder.getContext()
            .getAuthentication(), nullValue());
    }

    @Test
    public void givenPasswordResetTokenExists_whenRequestingChangePasswordPageWithNotMatchingTokenAndUserId_thenErrorMessageIsShown() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        final String token = RandomStringUtils.random(10);
        final PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);

        mockMvc.perform(get("/user/changePassword").param("id", RandomStringUtils.randomNumeric(5))
            .param("token", token))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")))
            .andExpect(flash().attributeExists("errorMessage"));
        assertThat(SecurityContextHolder.getContext()
            .getAuthentication(), nullValue());
    }

    @Test
    public void givenPasswordResetTokenExists_whenRequestingChangePasswordPageByExiredToken_thenErrorMessageIsShown() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        final String token = RandomStringUtils.random(10);
        final PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetToken.setExpiryDate(new Date(System.currentTimeMillis() - 1));
        passwordResetTokenRepository.save(passwordResetToken);

        mockMvc.perform(get("/user/changePassword").param("id", RandomStringUtils.randomNumeric(5))
            .param("token", token))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/login")))
            .andExpect(flash().attributeExists("errorMessage"));
        assertThat(SecurityContextHolder.getContext()
            .getAuthentication(), nullValue());
    }

    // Private Helper Methods

    private void assertThatNoUsersRegistered() {
        assertThat(getEmailsCount(), equalTo(0));
        assertThat(userRepository.count(), equalTo(0L));
    }

}
