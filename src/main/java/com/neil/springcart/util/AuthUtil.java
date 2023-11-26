package com.neil.springcart.util;

import com.neil.springcart.model.Admin;
import com.neil.springcart.repository.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthUtil {
    private final AdminRepository adminRepository;

    /**
     * Checks if the user making the request is an admin.
     * @return {@code true} if the request is from an admin, {@code false}
     * otherwise.
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails user) {
            String email = user.getUsername();
            return isAnAdminEmail(email);
        }
        return false;
    }

    /**
     * Checks if the given email is an admin email.
     * @param email The email of a user.
     * @return {@code true} if the email belongs to an admin, {@code false}
     * otherwise.
     */
    private boolean isAnAdminEmail(String email) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        return admin.isPresent();
    }
}
