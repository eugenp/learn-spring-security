package com.baeldung.lss.spring.converter;

import com.baeldung.lss.web.model.User;
import org.springframework.core.convert.converter.Converter;

public interface UserIdConverter extends Converter<String, User> {
}
