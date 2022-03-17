package com.baeldung.lss.security;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.provisioning.UserDetailsManagerResourceFactoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class UsersFromPropertiesFile {

     @Bean("fromPropFile")
     public FactoryBean<? extends UserDetailsService> userDetailsServiceBean() {
         return UserDetailsManagerResourceFactoryBean.fromResourceLocation("classpath:users.properties");
     }
 }
