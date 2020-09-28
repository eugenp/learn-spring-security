package com.baeldung.lss.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
class PathController {

    @Value("${google.auth.enabled}")
    private Boolean isGoogleAuthEnabled;

    @RequestMapping("/")
    public String home() {
        return "redirect:/user";
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("loginPage", "isGoogleAuthEnabled", isGoogleAuthEnabled);
    }

}