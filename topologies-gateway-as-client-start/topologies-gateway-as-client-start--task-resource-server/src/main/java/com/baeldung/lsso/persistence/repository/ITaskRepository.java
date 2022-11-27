package com.baeldung.lsso.persistence.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.baeldung.lsso.persistence.model.Task;

public interface ITaskRepository extends PagingAndSortingRepository<Task, Long> {

    Iterable<Task> findByProjectId(Long id);
}
