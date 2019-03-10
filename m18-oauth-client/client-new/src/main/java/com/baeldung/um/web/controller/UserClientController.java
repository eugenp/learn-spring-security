package com.baeldung.um.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.baeldung.um.web.model.User;

@Controller
public class UserClientController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> users = restTemplate.getForObject("http://localhost:8081/um-webapp-resource-server/api/users/", List.class);
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/addusers")
    public String addNewUser(Model model) {
        model.addAttribute("user", new User(0, "", ""));
        return "adduser";
    }

    @PostMapping("/users")
    public String saveUser(User user, Model model) {
        try {
            restTemplate.postForObject("http://localhost:8081/um-webapp-resource-server/api/users/", user, User.class);
            return "redirect:/users";
        } catch (final HttpServerErrorException e) {
            model.addAttribute("msg", e.getResponseBodyAsString());
            return "adduser";
        }
    }

}
