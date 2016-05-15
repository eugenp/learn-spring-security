package com.baeldung.lss.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import com.baeldung.lss.security.RealTimeLockVoter;
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
            .anyRequest().authenticated().accessDecisionManager(unnanimous())
        
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
    public AccessDecisionManager unnanimous(){
        final List<AccessDecisionVoter<? extends Object>> voters = Lists.newArrayList(new RoleVoter(), new AuthenticatedVoter(), new RealTimeLockVoter(), new WebExpressionVoter());
        return new UnanimousBased(voters);
    }

}
