package com.baeldung.lss.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.provisioning.UserDetailsManagerResourceFactoryBean;

@Configuration
public class LssPropertiesUserDetailsService {

    @Bean
    public UserDetailsManagerResourceFactoryBean userDetailsServiceBean() {
        return UserDetailsManagerResourceFactoryBean.fromResourceLocation("classpath:users.properties");
    }

}
