package com.baeldung.lss.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.baeldung.lss.model.User;
import com.baeldung.lss.repository.UserRepository;

@Component
public class MessageConverter implements Converter<String, User> {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public User convert(String id) {
		return userRepository.findUser(Long.valueOf(id));
	}
}