package com.baeldung.lsso.service;

import java.util.Optional;

import com.baeldung.lsso.persistence.model.Project;

public interface IProjectService {

    Optional<Project> findById(Long id);

    Project save(Project project);

    Iterable<Project> findAll();

}
