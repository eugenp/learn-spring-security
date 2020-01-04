package com.baeldung.lsso.web.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpServerErrorException;

import com.baeldung.lsso.web.model.Project;

@Controller
public class ProjectClientController {

    @GetMapping("/projects")
    public String getProjects(Model model) {
        List<Project> projects = null;
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
            
            return "redirect:/projects";
        } catch (final HttpServerErrorException e) {
            model.addAttribute("msg", e.getResponseBodyAsString());
            return "addproject";
        }
    }
}
