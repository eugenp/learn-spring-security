package com.baeldung.um.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.baeldung.um.persistence.UserRepository;
import com.baeldung.um.service.IUserService;
import com.baeldung.um.validation.EmailExistsException;
import com.baeldung.um.web.model.User;

@Controller
@RequestMapping("/api/user")
class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    UserRestController() {
        super();
    }

    //

    @RequestMapping
    @ResponseBody
    public List<User> list() {
        return userRepository.findAll();
    }

    @RequestMapping("{id}")
    @ResponseBody
    public User view(@PathVariable("id") User user) {
        return user;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid final User user) throws EmailExistsException {
        return userService.registerNewUser(user);
    }

    @RequestMapping(value = "delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") final Long id) {
        userRepository.delete(id);
    }

}
