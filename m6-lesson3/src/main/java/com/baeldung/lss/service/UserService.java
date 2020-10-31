package com.baeldung.lss.service;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.PasswordResetTokenRepository;
import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.validation.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public User registerNewUser(final User user) throws EmailExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + user.getEmail());
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public void changeUserPassword(final User user, final String password) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private boolean emailExist(final String email) {
        final User user = userRepository.findByEmail(email);
        return user != null;
    }

}
