package com.baeldung.lsso.persistence.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsso.persistence.model.Project;

public interface IProjectRepository extends PagingAndSortingRepository<Project, Long>, CrudRepository<Project, Long> {
}
