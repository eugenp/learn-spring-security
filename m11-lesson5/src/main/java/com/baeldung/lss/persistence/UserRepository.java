package com.baeldung.lss.persistence;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.baeldung.lss.model.User;

public interface UserRepository extends ElasticsearchRepository<User, String> {

    User findByEmail(String email);

}
