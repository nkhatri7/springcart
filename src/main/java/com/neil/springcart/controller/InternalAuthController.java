package com.neil.springcart.controller;

import com.neil.springcart.dto.AdminAuthResponse;
import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.model.Admin;
import com.neil.springcart.service.InternalAuthService;
import com.neil.springcart.util.HttpUtil;
import com.neil.springcart.util.JwtUtil;
import com.neil.springcart.util.mapper.AdminMapper;
import io.swagger.v3.oas.annotations.Operation;
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
    private final HttpUtil httpUtil;
    private final JwtUtil jwtUtil;
    private final AdminMapper adminMapper;

    /**
     * Handles incoming requests for the internal /login endpoint which
     * authenticates an admin user.
     * @param request The request body.
     * @return The admin details with a JWT token in the header.
     */
    @Operation(summary = "Authenticates an admin account")
    @PostMapping("/login")
    public ResponseEntity<AdminAuthResponse> handleAdminLogin(
            @RequestBody @Valid LoginRequest request) {
        log.info("POST /internal/auth/login");

        Admin admin = internalAuthService.authenticateAdmin(request);
        log.info("Admin signed in (ID: {})", admin.getId());
        String token = jwtUtil.generateToken(admin);

        HttpHeaders headers = httpUtil.generateAuthorizationHeader(token);
        AdminAuthResponse response = adminMapper.mapToResponse(admin);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(response);
    }
}
