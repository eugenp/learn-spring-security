package com.baeldung.lss.spring;

import java.util.Collection;
import java.util.List;

import com.baeldung.lss.security.LockedUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import com.google.common.collect.Lists;

@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    public LssSecurityConfig() {
        super();
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off 
        auth.
            inMemoryAuthentication()
            .withUser("user").password("pass").roles("USER").and()
            .withUser("admin").password("pass").roles("ADMIN")
            ;
    } // @formatter:on

    @Override
    protected void configure(HttpSecurity http) throws Exception { // @formatter:off
        http
        .authorizeRequests()
            
            .antMatchers("/secured").access("hasRole('ADMIN')")
            .anyRequest().authenticated().accessDecisionManager(unanimous())
        
        .and()
        .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin")

        .and()
        .logout().permitAll().logoutUrl("/logout")
        
        .and()
        .csrf().disable()
        ;
    }
    
    // 
    
    @Bean
    public AccessDecisionManager unanimous(){
        final List<AccessDecisionVoter<? extends Object>> decisionVoters = Lists.newArrayList(new RoleVoter(), new AuthenticatedVoter(), new RealTimeLockVoter(), new WebExpressionVoter());

        return new UnanimousBased(decisionVoters);
    }

    class RealTimeLockVoter implements AccessDecisionVoter<Object> {

        @Override
        public boolean supports(ConfigAttribute attribute) {
            return true;
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }

        @Override
        public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
            if (LockedUsers.isLocked(authentication.getName())) {
                return ACCESS_DENIED;
            }

            return ACCESS_GRANTED;
        }

    }
}
