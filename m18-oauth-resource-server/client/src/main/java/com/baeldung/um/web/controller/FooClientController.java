package com.baeldung.um.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.baeldung.um.web.model.Foo;

@Controller
@RequestMapping("/foos")
public class FooClientController {

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${oauth.resourceServerBase}")
    private String resourceServerBase;

    @GetMapping("/{id}")
    public String getFooResource(@PathVariable long id, Model model) {
        Foo foo = restTemplate.getForEntity(resourceServerBase + "/api/foos/" + id, Foo.class).getBody();
        model.addAttribute("foo", foo);
        return "foo";
    }
    
    @PostMapping
    public String addNewFoo(Foo foo, Model model) {
        Foo created = restTemplate.postForEntity(resourceServerBase + "/api/foos/", foo, Foo.class).getBody();
        model.addAttribute("foo", created);
        return "foo";
    }

}
