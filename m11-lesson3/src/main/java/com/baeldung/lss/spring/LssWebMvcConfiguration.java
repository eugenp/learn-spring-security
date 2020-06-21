package com.baeldung.lss.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
<<<<<<< HEAD
public class LssWebMvcConfiguration extends WebMvcConfigurerAdapter {
=======
public class LssWebMvcConfiguration implements WebMvcConfigurer {
>>>>>>> 0abf5981dcf0c21c5ac27e4dec781f80ea6315a8

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/" };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
            .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login")
            .setViewName("loginPage");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}