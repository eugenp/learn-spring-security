package com.baeldung.lss.service;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserDao;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Stateless(name = "userService", mappedName = "userService")
@Local
public class UserService implements UserServiceInterface {

    @Inject
    UserDao userDAO;

    private PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void createTestUsers() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEnabled(true);
        final String encodedPassword = passwordEncoder.encode("pass");
        user.setPassword(encodedPassword);
        user.setPasswordConfirmation(encodedPassword);
        userDAO.createUser(user);

        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setEnabled(true);
        user2.setPassword(encodedPassword);
        user2.setPasswordConfirmation(encodedPassword);
        userDAO.createUser(user2);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

}
