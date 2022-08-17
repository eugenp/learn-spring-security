package com.baeldung.lss.spring;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import com.baeldung.lss.security.RealTimeLockVoter;
import com.google.common.collect.Lists;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan("com.baeldung.lss.web")
@EnableJpaRepositories("com.baeldung.lss")
@EntityScan("com.baeldung.lss.web.model")
public class LssApp5 {

    @Bean
    public AccessDecisionManager unnanimous() {
        final List<AccessDecisionVoter<? extends Object>> voters = Lists.newArrayList(new RoleVoter(), new AuthenticatedVoter(), new RealTimeLockVoter(), new WebExpressionVoter());
        return new UnanimousBased(voters);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[] { LssApp5.class, LssSecurityConfig.class, LssWebMvcConfiguration.class }, args);
    }

}
