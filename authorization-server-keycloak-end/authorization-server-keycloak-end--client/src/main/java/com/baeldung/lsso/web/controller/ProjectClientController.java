package com.baeldung.lsso.web.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import com.baeldung.lsso.web.model.Project;

@Controller
public class ProjectClientController {

    @Autowired
    private WebClient webClient;

    @GetMapping("/projects")
    public String getProjects(Model model) {
        List<Project> projects = this.webClient.get()
            .uri("http://localhost:8081/lsso-resource-server/api/projects/")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Project>>() {
            })
            .block();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/addproject")
    public String addNewProject(Model model) {
        model.addAttribute("project", new Project(0L, "", LocalDate.now()));
        return "addproject";
    }

    @PostMapping("/projects")
    public String saveProject(Project project, Model model) {
        try {
            this.webClient.post()
                .uri("http://localhost:8081/lsso-resource-server/api/projects/")
                .bodyValue(project)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            return "redirect:/projects";
        } catch (final HttpServerErrorException e) {
            model.addAttribute("msg", e.getResponseBodyAsString());
            return "addproject";
        }
    }

}
