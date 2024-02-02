package com.baeldung.lss.persistence;

import java.util.List;

import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import com.baeldung.lss.model.User;

@Named
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    // write

    public void createUser(User user) {
        em.persist(user);
    }

    // read

    public List<User> getAllUsers() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    public User findByEmail(String email) {
        User user = (User) em.createQuery("SELECT u FROM User u where u.email = :value1")
            .setParameter("value1", email)
            .getSingleResult();
        return user;
    }

}
