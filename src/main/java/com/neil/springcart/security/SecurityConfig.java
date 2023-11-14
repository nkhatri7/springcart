package com.neil.springcart.security;

import com.neil.springcart.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security config class to set up security services in the application.
 */
@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final CustomerRepository customerRepository;

    /**
     * Implementation of UserDetailsService and the loadUserByUsername method.
     * Searches the Customer repository for a customer with the email (username)
     * @return A UserDetailsService object for the user making the request.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> customerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Customer not found"
                ));
    }

    /**
     * Implementation of AuthenticationProvider. Sets up the
     * AuthenticationProvider with the custom UserDetailsService and
     * PasswordEncoder implementations.
     * @return An AuthenticationProvider object.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Implementing an Authentication Manager for the application.
     * @param config The authentication configuration.
     * @return An AuthenticationManager object.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Implementing a Password Encoder for this application with the BCrypt
     * password encoder.
     * @return A PasswordEncoder object.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
