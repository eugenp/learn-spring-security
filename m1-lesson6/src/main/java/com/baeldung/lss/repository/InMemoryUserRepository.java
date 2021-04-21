package com.baeldung.lss.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.baeldung.lss.model.User;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private static AtomicLong counter = new AtomicLong();

    private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public Iterable<User> findAll() {
        return this.users.values();
    }

    @Override
    public User save(User user) {
        Long id = user.getId();
        if (id == null) {
            id = counter.incrementAndGet();
            user.setId(id);
        }
        this.users.put(id, user);
        return user;
    }

    @Override
    public User findUser(Long id) {
        return this.users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        this.users.remove(id);
    }

}
