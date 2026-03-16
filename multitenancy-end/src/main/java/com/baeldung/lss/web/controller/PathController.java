package com.baeldung.lss.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class PathController {

    //

    @RequestMapping("/")
    public String home() {
        return "redirect:/user";
    }

}
