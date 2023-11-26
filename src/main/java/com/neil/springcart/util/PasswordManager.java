package com.neil.springcart.util;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PasswordManager {
    private final PasswordEncoder passwordEncoder;

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
