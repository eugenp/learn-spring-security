package com.baeldung.lss.web.controller;

import com.baeldung.lss.service.RunAsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/runas")
class RunAsController {

    @Autowired
    private RunAsService runAsService;

    @Secured({ "ROLE_USER", "RUN_AS_REPORTER" })
    @RequestMapping
    @ResponseBody
    public String tryRunAs() {
        final Authentication auth = runAsService.getCurrentUser();
        auth.getAuthorities()
                .forEach(a -> System.out.println(a.getAuthority()));
        return "Current User Authorities inside this RunAS method only " + auth.getAuthorities()
                .toString();
    }

}