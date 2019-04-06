package com.baeldung.lss.service;

import com.baeldung.lss.persistence.model.User;
import com.baeldung.lss.validation.EmailExistsException;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

}
