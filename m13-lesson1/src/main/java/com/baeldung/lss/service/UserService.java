package com.baeldung.lss.service;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserDao;

@Stateless(name = "userService", mappedName = "userService")
@Local
public class UserService implements UserServiceInterface {

    @Inject
    UserDao userDAO;

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void createTestUsers() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setPassword("pass");
        user.setPasswordConfirmation("pass");
        userDAO.createUser(user);

        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setEnabled(true);
        user2.setPassword("pass");
        user2.setPasswordConfirmation("pass");
        userDAO.createUser(user2);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

}
