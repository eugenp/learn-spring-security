package com.baeldung.lss.spring;

import com.baeldung.lss.security.CustomAuthenticationProvider;
import com.baeldung.lss.security.CustomWebAuthenticationDetailsSource;
import com.baeldung.lss.security.LssLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LssLoggingFilter lssLoggingFilter;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Value("${google.auth.enabled}")
    private boolean isGoogleAuthEnabled;

    public LssSecurityConfig() {
        super();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off
        if (isGoogleAuthEnabled) {
            auth.authenticationProvider(customAuthenticationProvider);
        } else {
            auth.authenticationProvider(daoAuthenticationProvider());
        }
        auth.authenticationProvider(runAsAuthenticationProvider());

    } // @formatter:on

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationProvider runAsAuthenticationProvider() {
        final RunAsImplAuthenticationProvider authProvider = new RunAsImplAuthenticationProvider();
        authProvider.setKey("MyRunAsKey");
        return authProvider;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception { // @formatter:off
        http
                .addFilterBefore(lssLoggingFilter, AnonymousAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/signup",
                        "/user/register",
                        "/registrationConfirm*",
                        "/badUser*",
                        "/forgotPassword*",
                        "/user/resetPassword*",
                        "/user/changePassword*",
                        "/user/savePassword*",
                        "/code*",
                        "/isUsing2FA*",
                        "/js/**").permitAll()
                .antMatchers("/secured").hasRole("USER")//access("hasRole('USER')")
                .anyRequest().authenticated()

                .and()
                .formLogin().
                loginPage("/login").permitAll().
                loginProcessingUrl("/doLogin")
                .authenticationDetailsSource(authenticationDetailsSource)

                .and()
                .rememberMe()
                .key("lssAppKey")
                .tokenValiditySeconds(604800) // 1 week = 604800
                .tokenRepository(persistentTokenRepository())

                .and()
                .logout().permitAll().logoutUrl("/logout")

                .and()
                .sessionManagement().maximumSessions(1)
                .sessionRegistry(sessionRegistry()).and().sessionFixation().none()

                .and()
                .csrf().disable()
        ;
    } // @formatter:on

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
}