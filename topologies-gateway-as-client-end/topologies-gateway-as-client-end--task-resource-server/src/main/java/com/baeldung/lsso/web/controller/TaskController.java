package com.baeldung.lsso.web.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baeldung.lsso.persistence.model.Task;
import com.baeldung.lsso.service.ITaskService;
import com.baeldung.lsso.web.dto.TaskDto;

@RestController
@RequestMapping(value = "/api/tasks")
public class TaskController {

    private ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Collection<TaskDto> findByProjectId(@RequestParam("projectId") Long id) {
        Iterable<Task> tasks = taskService.findByProjectId(id);
        Collection<TaskDto> taskDtos = new ArrayList<>();
        tasks.forEach(p -> taskDtos.add(convertToDto(p)));
        return taskDtos;
    }

    protected TaskDto convertToDto(Task entity) {
        return new TaskDto(entity.getId(), entity.getName(), entity.getDescription(), entity.getDateCreated(), entity.getDueDate());
    }
}