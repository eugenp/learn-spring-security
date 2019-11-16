package com.baeldung.um.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baeldung.um.persistence.UserRepository;
import com.baeldung.um.validation.EmailExistsException;
import com.baeldung.um.web.model.User;

@Service
@Transactional
class UserService implements IUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // write

    @Override
    public User registerNewUser(final User user) throws EmailExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + user.getEmail());
        }
        final String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setPasswordConfirmation(encodedPassword);
        return repository.save(user);
    }

    // read

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    //

    private boolean emailExist(String email) {
        final User user = repository.findByEmail(email);
        return user != null;
    }

}
