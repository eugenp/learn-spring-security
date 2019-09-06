package com.baeldung.lss.test.integration.web.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import com.baeldung.lss.model.User;
import com.baeldung.lss.validation.EmailExistsException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("unchecked")
public class UserControllerIntegrationTest extends AbstractBaseControllerIntegrationTest {

    @Override
    protected Boolean startSmtpServer() {
        return false;
    }

    // Tests

    // list: GET /user
    @Test
    public void givenNoUsersExist_whenListingAllUsers_thenEmptyListIsShows() throws Exception {
        mockMvc.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/list")))
            .andExpect(model().attribute("users", empty()));
    }

    @Test
    public void givenUsersExist_whenListingAllUsers_thenUsersAreShown() throws Exception, EmailExistsException {
        registerNewUser();
        final String email2 = "2" + VALUE_DEFAULT_USER_EMAIL;
        registerNewUser(email2);

        final ModelAndView modelAndView = mockMvc.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/list")))
            .andReturn()
            .getModelAndView();

        final List<User> users = (List<User>) modelAndView.getModel()
            .get("users");
        assertThat(users, hasSize(2));
        final User persistedUser1 = users.stream()
            .filter(user -> user.getEmail()
                .equals(VALUE_DEFAULT_USER_EMAIL))
            .findFirst()
            .get();
        checkUser(persistedUser1, VALUE_DEFAULT_USER_EMAIL);
        final User persistedUser2 = users.stream()
            .filter(user -> user.getEmail()
                .equals(email2))
            .findFirst()
            .get();
        checkUser(persistedUser2, email2);
    }

    // view: GET /user/{id}

    @Test
    public void givenUsersExist_whenViewingUser_thenUserIsShown() throws EmailExistsException, Exception {
        final User user = registerNewUser();
        registerNewUser("2" + VALUE_DEFAULT_USER_EMAIL);

        final ModelAndView modelAndView = mockMvc.perform(get("/user/" + user.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/view")))
            .andReturn()
            .getModelAndView();

        final User persistedUser = (User) modelAndView.getModel()
            .get("user");
        checkUser(persistedUser, VALUE_DEFAULT_USER_EMAIL);
    }

    @Test
    public void givenUsersNotExist_whenViewingUser_then404() throws EmailExistsException, Exception {
        mockMvc.perform(get("/user/" + RandomStringUtils.random(5)))
            .andExpect(status().isNotFound());
    }

    // create: POST /user

    @Test
    public void whenCreatingUser_thenUserIsCreated() throws Exception {
        mockMvc.perform(post("/user").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isFound())
            .andExpect(view().name(startsWith("redirect:/user/")))
            .andExpect(model().attributeExists("user.id"));

        final User persistedUser = userService.findUserByEmail(VALUE_DEFAULT_USER_EMAIL);
        checkUser(persistedUser, VALUE_DEFAULT_USER_EMAIL);
    }

    @Test
    public void whenCreatingUserProvidingNoEmail_thenErrorIsShown() throws Exception {
        mockMvc.perform(post("/user").param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/form")))
            .andExpect(model().attributeExists("formErrors"));

        assertThat(userRepository.count(), equalTo(0L));
    }

    @Test
    public void whenCreatingUserProvidingNoPassword_thenErrorIsShown() throws Exception {
        mockMvc.perform(post("/user").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/form")))
            .andExpect(model().attributeExists("formErrors"));

        assertThat(userRepository.count(), equalTo(0L));
    }

    @Test
    public void whenCreatingUserProvidingInvalidEmail_thenErrorIsShown() throws Exception {
        mockMvc.perform(post("/user").param("email", "not a email")
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/form")))
            .andExpect(model().attributeExists("formErrors"));

        assertThat(userRepository.count(), equalTo(0L));
    }

    @Test
    public void whenCreatingUserProvidingInvalidPassword_thenErrorIsShown() throws Exception {
        mockMvc.perform(post("/user").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", "invalid password")
            .param("passwordConfirmation", "invalid password"))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/form")))
            .andExpect(model().attributeExists("formErrors"));

        assertThat(userRepository.count(), equalTo(0L));
    }

    @Test
    public void whenCreatingUserProvidingNotMatchingPasswords_thenErrorIsShown() throws Exception {
        mockMvc.perform(post("/user").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", "not matching password"))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/form")))
            .andExpect(model().attributeExists("formErrors"));

        assertThat(userRepository.count(), equalTo(0L));
    }

    @Test
    public void givenUserExists_whenCreatingUserWithEmailDuplicate_thenErrorIsShown() throws Exception, EmailExistsException {
        registerNewUser();

        mockMvc.perform(post("/user").param("email", VALUE_DEFAULT_USER_EMAIL)
            .param("password", VALUE_DEFAULT_USER_PASSWORD)
            .param("passwordConfirmation", VALUE_DEFAULT_USER_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(equalTo("tl/form")))
            .andExpect(model().attributeHasFieldErrors("user", "email"));

        assertThat(userRepository.count(), equalTo(1L));
    }

    // delete: GET /user/delete/{id}

    @Test
    public void givenUserExists_whenDeletingUser_thenUserIsDeleted() throws EmailExistsException, Exception {
        final User user = registerNewUser();

        mockMvc.perform(get("/user/delete/" + user.getId()))
            .andExpect(status().isFound())
            .andExpect(view().name(equalTo("redirect:/")));

        assertThat(userRepository.count(), equalTo(0L));
    }

    @Test
    public void givenNoUsersExist_whenDeletingUser_then404() throws Exception {
        mockMvc.perform(get("/user/delete/" + RandomStringUtils.random(5)))
            .andExpect(status().isNotFound());
    }

    // Private Helper Methods

    private void checkUser(final User user, final String email) {
        assertThat(user, notNullValue());
        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(email));
        assertThat(user.getCreated(), notNullValue());
        assertThat(user.getEnabled(), equalTo(false));
        assertThat(user.getPassword(), equalTo(VALUE_DEFAULT_USER_PASSWORD));
    }

}
