package com.baeldung.lss.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    public LssSecurityConfig() {
        super();
    }

    /*//

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off 
        auth.
            inMemoryAuthentication().
            withUser("user").password("pass").
            roles("USER");
    } // @formatter:on
*/

    @Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {// @formatter:off 
        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth
                .ldapAuthentication()
                    .userSearchBase("ou=people")
                    .userSearchFilter("(uid={0})")
                    .groupSearchBase("ou=roles")
                    .groupSearchFilter("member={0}")
                    .contextSource()
                    .root("dc=springframework,dc=org")
                    .ldif("classpath:users.ldif")
                    .and()
                    .passwordCompare()
                    .passwordEncoder(passwordEncoder())
                    .passwordAttribute("userPassword");
                    //.url("ldap://localhost:10389/dc=springframework,dc=org");
        }
    }// @formatter:on

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception { // @formatter:off
        http
        .authorizeHttpRequests()
                .requestMatchers(regexMatcher("/user\\?form")).hasAnyRole("ADMIN")
                .requestMatchers("/user").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/user/**").hasRole("ADMIN")
        .and()
        .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin").
            defaultSuccessUrl("/user", true)

        .and()
        .logout().permitAll().logoutUrl("/logout")
        
        .and()
        .csrf().disable();
        return http.build();
    } // @formatter:on

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
