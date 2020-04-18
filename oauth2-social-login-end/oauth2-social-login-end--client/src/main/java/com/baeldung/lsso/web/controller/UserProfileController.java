package com.baeldung.lsso.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
public class UserProfileController {

    @Autowired
    private WebClient webClient;

    @GetMapping("/user")
    public String user(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("name", principal.getAttribute("name"));
        model.addAttribute("id", principal.getAttribute("login"));
        model.addAttribute("img", principal.getAttribute("avatar_url"));

        String repoUrl = principal.getAttribute("repos_url");

        List<Map<Object, Object>> repos = this.webClient.get()
            .uri(repoUrl)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Map<Object, Object>>>() {
            })
            .block();

        model.addAttribute("repos", repos);
        return "user";
    }

}
