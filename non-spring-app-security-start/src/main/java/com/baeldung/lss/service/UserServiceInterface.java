package com.baeldung.lss.service;

import java.util.List;

import com.baeldung.lss.model.User;

public interface UserServiceInterface {

    public List<User> getAllUsers();

    public void createTestUsers();

    public User findByEmail(String email);

}
