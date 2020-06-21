package com.baeldung.lss.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserRepository;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepository;

    public LssSecurityConfig() {
        super();
    }

    //

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {// @formatter:off
//        final UserBuilder userBuilder = org.springframework.security.core.userdetails.User.builder();
//        final UserDetails user = userBuilder.username("user").password(passwordEncoder().encode("pass")).roles("USER").build();
//        final UserDetails admin = userBuilder.username("admin").password(passwordEncoder().encode("pass")).roles("ADMIN").build();
//        
//        final UserDetailsService userDetailsService = new InMemoryUserDetailsManager(user, admin);
//        
//        auth.userDetailsService(userDetailsService);
//    }// @formatter:on

    @Override
    protected void configure(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeRequests()
                .antMatchers("/badUser*","/js/**").permitAll()
                .anyRequest().authenticated()

        .and()
        .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin")
        
        .and()
        .logout().permitAll().logoutUrl("/logout")
        
        .and().sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry()).and().sessionFixation().none()

        .and()
        .csrf().disable()
        ;
    } // @formatter:on

    @PostConstruct
    private void saveTestUser() {
        final User user = new User();
        user.setEmail("test@email.com");
        user.setPassword(passwordEncoder().encode("pass"));
        userRepository.save(user);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
