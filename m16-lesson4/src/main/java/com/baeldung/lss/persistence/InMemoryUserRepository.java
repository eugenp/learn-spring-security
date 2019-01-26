package com.baeldung.lss.persistence;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.baeldung.lss.web.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class InMemoryUserRepository implements UserRepository {

    private static AtomicLong counter = new AtomicLong();

    private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<Long, User>();

    //

    @Override
    public Flux<User> findAll() {
        return Flux.fromIterable(this.users.values());
    }

    @Override
    public Mono<User> save(User user) {
        Long id = user.getId();
        if (id == null) {
            id = counter.incrementAndGet();
            user.setId(id);
        }
        this.users.put(id, user);
        return Mono.just(user);
    }

    @Override
    public Mono<User> findUser(Long id) {
        return Mono.just(this.users.get(id));
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        this.users.remove(id);
        return Mono.empty();
    }

}
