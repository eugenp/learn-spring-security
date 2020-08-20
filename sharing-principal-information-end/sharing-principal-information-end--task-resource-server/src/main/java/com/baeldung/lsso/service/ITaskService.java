package com.baeldung.lsso.service;

import com.baeldung.lsso.persistence.model.Task;

public interface ITaskService {

    Iterable<Task> findByProjectId(Long id);

}
