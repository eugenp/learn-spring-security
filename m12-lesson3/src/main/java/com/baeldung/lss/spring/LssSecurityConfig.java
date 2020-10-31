package com.baeldung.lss.spring;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.security.CustomWebAuthenticationDetailsSource;
import com.baeldung.lss.web.model.User;
import com.twilio.sdk.TwilioRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    private AuthenticationProvider authProvider;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private UserRepository userRepository;

    @Value("${twilio.sid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

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
                .antMatchers("/signup", "/user/register","/code*","/isUsing2FA*").permitAll()
                .anyRequest().authenticated()
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

    //

    @Bean
    public TwilioRestClient twilioRestClient() {
        return new TwilioRestClient(accountSid, authToken);
    }

    @PostConstruct
    private void saveTestUser() {
        final User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("pass");
        user.setPasswordConfirmation("pass");
        userRepository.save(user);
    }

}
