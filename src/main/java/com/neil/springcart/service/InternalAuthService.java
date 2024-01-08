package com.neil.springcart.service;

import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Admin;
import com.neil.springcart.repository.AdminRepository;
import com.neil.springcart.util.PasswordManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InternalAuthService {
    private final AdminRepository adminRepository;
    private final PasswordManager passwordManager;

    /**
     * Checks if the email and password of the admin are valid.
     * @param request The LoginRequest containing the user email and password.
     * @return The admin data if the login details are valid.
     */
    public Admin authenticateAdmin(LoginRequest request) {
        // Check if admin with email exists
        Admin admin = getAdminByEmail(request.email().trim());
        // Check if password is valid
        String password = request.password().trim();
        if (!passwordManager.isPasswordValid(password, admin.getPassword())) {
            throw new BadRequestException("Password is incorrect");
        }
        return admin;
    }

    private Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElseThrow(() ->
                new BadRequestException("Account with this email doesn't exist")
        );
    }
}
