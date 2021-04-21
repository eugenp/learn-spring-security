package com.baeldung.lss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.baeldung.lss.converter.MessageConverter;

@EnableWebMvc
@Configuration
public class LssWebMvcConfiguration implements WebMvcConfigurer {
    
	@Autowired
	private MessageConverter converter;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("loginPage");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }   
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
    	registry.addConverter(converter);
    }
    
}