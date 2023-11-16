package com.neil.springcart.service;

import com.neil.springcart.dto.AdminAuthResponse;
import com.neil.springcart.model.Admin;
import com.neil.springcart.repository.AdminRepository;
import com.neil.springcart.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class InternalAuthService extends RootAuthService {
    @Autowired
    private final AdminRepository adminRepository;

    public InternalAuthService(PasswordEncoder passwordEncoder,
                               JwtUtils jwtUtils,
                               AdminRepository adminRepository) {
        super(passwordEncoder, jwtUtils);
        this.adminRepository = adminRepository;
    }

    /**
     * Searches the database for an admin with the given email.
     * @param email The email of an admin.
     * @return An Admin if one exists with that email, otherwise it is empty.
     */
    public Optional<Admin> getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public AdminAuthResponse mapToAdminAuthResponse(Admin admin) {
        return new AdminAuthResponse(admin.getEmail());
    }
}
