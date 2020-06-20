package com.baeldung.lss.persistence;

import com.baeldung.lss.web.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Flux<User> findAll();

    Mono<User> save(User user);

    Mono<User> findUser(Long id);

    Mono<Void> deleteUser(Long id);

}
