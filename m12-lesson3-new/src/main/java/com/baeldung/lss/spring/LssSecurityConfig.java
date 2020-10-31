package com.baeldung.lss.spring;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.security.CustomWebAuthenticationDetailsSource;
import com.baeldung.lss.web.model.User;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan({ "org.baeldung.lss.security" })
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${twilio.sid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

    @Autowired
    private AuthenticationProvider authProvider;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private UserRepository userRepository;

    public LssSecurityConfig() {
        super();
    }

    //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeRequests()
                .antMatchers("/signup", "/user/register").permitAll()
                .anyRequest().hasRole("USER")
        .and()
        .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin")
            .authenticationDetailsSource(authenticationDetailsSource)
        .and()
        .logout().permitAll().logoutUrl("/logout")
        .and()
        .csrf().disable()
        ;
    } // @formatter:on

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class BasicSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/code*").authorizeRequests().anyRequest().hasRole("TEMP_USER").and().httpBasic();
        }
    }

    @PostConstruct
    private void init() {
        Twilio.init(accountSid, authToken);

        final User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("pass");
        user.setPasswordConfirmation("pass");
        userRepository.save(user);
    }

}
