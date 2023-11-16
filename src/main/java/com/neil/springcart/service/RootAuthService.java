package com.neil.springcart.service;

import com.neil.springcart.security.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RootAuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Generates a JWT token for the given user
     * @param user A UserDetails implementation (e.g. Customer or Admin)
     * @return A JWT token signed with the user's credentials.
     */
    public String generateUserToken(UserDetails user) {
        return jwtUtils.generateToken(user);
    }

    /**
     * Checks if the given raw password is the same as the given encrypted
     * password.
     * @param password The raw password.
     * @param encryptedPassword The encrypted password.
     * @return {@code true} if the raw password matches the encrypted password,
     * {@code false} otherwise.
     */
    public boolean isPasswordValid(String password, String encryptedPassword) {
        return passwordEncoder.matches(password, encryptedPassword);
    }

    /**
     * Encrypts the given raw password.
     * @param rawPassword The password to be encrypted.
     * @return An encrypted password.
     */
    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
