package com.baeldung.lss.spring;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class LssWebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("loginPage");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter((Converter<String, User>) id -> userRepository.findUser(Long.valueOf(id)));

    }

}