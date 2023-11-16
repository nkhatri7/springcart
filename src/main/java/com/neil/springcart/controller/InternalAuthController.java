package com.neil.springcart.controller;

import com.neil.springcart.dto.AdminAuthResponse;
import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Admin;
import com.neil.springcart.service.InternalAuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to handle incoming requests for internal authentication requests
 * (i.e. Admins trying to authenticate).
 */
@RestController
@RequestMapping("/internal/auth")
@AllArgsConstructor
@Slf4j
public class InternalAuthController {
    private final InternalAuthService internalAuthService;

    /**
     * Handles incoming requests for the internal /login endpoint which
     * authenticates an admin user.
     * @param loginRequest The request body.
     * @return The admin details with a JWT token in the header.
     * @throws BadRequestException If an account with the email from the request
     * doesn't exist.
     * @throws BadRequestException If the password from the request is
     * incorrect.
     */
    @PostMapping("/login")
    public ResponseEntity<AdminAuthResponse> handleAdminLogin(
            @RequestBody @Valid LoginRequest loginRequest) {
        log.info("/internal/auth/login reached");
        // Check if account with email exists
        Admin admin = internalAuthService.getAdminByEmail(loginRequest.email())
                .orElseThrow(() -> new BadRequestException(
                        "Account with this email doesn't exist"));
        // Verify password
        if (!internalAuthService.isPasswordValid(loginRequest.password(),
                admin.getPassword())) {
            throw new BadRequestException("Password is incorrect");
        }
        // Generate token and response
        String token = internalAuthService.generateUserToken(admin);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        AdminAuthResponse response = internalAuthService
                .mapToAdminAuthResponse(admin);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(response);
    }
}
