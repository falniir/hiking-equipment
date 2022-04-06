package no.ntnu.xxs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Creates AuthenticationManager - sets up authentication type
 * The @EnableWebSecurity is needed to specify that this is a web security configuration class
 * The @EnableGlobalMethodSecurity is needed so that each endpoint can specify which role it requires
 */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    /**
     * A service providing our users from the database
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * This method is called automatically by the framework, so that its knows what type of authentication to use.
     * We also tell the framework to load the users from a database
     *
     * @param auth Authentication builder
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    /**
     * Configure the authorization rules
     *
     * @param http HTTP Security object
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Set up the authorization requests. Most restrictive should be specified first, then continue is a descending order
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/users").hasRole("ADMIN")
                .antMatchers("/api/products").hasAnyRole("USER", "ADMIN")
                .antMatchers("/").permitAll()
                .and().formLogin();
    }

    /**
     * This method is executed so that the framework knows what encryption to use when checking for password
     *
     * @return The password encryptor
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
