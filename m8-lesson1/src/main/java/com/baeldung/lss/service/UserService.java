package com.baeldung.lss.service;

import javax.transaction.Transactional;

import com.baeldung.lss.model.PasswordResetToken;
import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.PasswordResetTokenRepository;
import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.validation.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Override
    public User registerNewUser(final User user) throws EmailExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + user.getEmail());
        }
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
        userRepository.save(user);
    }

    private boolean emailExist(final String email) {
        final User user = userRepository.findByEmail(email);
        return user != null;
    }

}
