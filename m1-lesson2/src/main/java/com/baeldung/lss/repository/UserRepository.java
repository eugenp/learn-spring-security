package com.baeldung.lss.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.baeldung.lss.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
