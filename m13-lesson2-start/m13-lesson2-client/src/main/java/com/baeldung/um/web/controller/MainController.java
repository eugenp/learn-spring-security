package com.baeldung.um.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.baeldung.um.web.model.User;

@Controller
class MainController {

    @Autowired
    private RestTemplate restTemplate;

    //

    @RequestMapping("/user")
    public ModelAndView list() {
        final List<User> users = restTemplate.getForObject("http://localhost:8081/um-webapp/api/users", List.class);
        return new ModelAndView("list", "users", users);
    }

}
