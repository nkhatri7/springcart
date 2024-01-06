package com.neil.springcart.controller;

import com.neil.springcart.dto.CustomerResponse;
import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.service.CustomerAuthService;
import com.neil.springcart.util.HttpUtil;
import com.neil.springcart.util.JwtUtil;
import com.neil.springcart.util.mapper.CustomerMapper;
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
 * A controller to handle incoming auth related requests for the /api/v1/auth
 * endpoint.
 */
@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Slf4j
public class CustomerAuthController {
    private final CustomerAuthService customerAuthService;
    private final JwtUtil jwtUtil;
    private final CustomerMapper customerMapper;

    /**
     * Handles incoming requests for the /register endpoint which registers a
     * new customer in the system.
     * @param request The request body.
     * @return The new user data with a JWT token for the customer creating an
     * account.
     */
    @Operation(summary = "Creates a customer account")
    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> handleCustomerRegistration(
            @RequestBody @Valid RegisterRequest request) {
        log.info("POST {}", HttpUtil.getCurrentRequestPath());

        Customer customer = customerAuthService.createCustomer(request);
        log.info("Customer created (ID: {})", customer.getId());
        String token = jwtUtil.generateToken(customer);

        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(token);
        CustomerResponse response = customerMapper.mapToResponse(customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(response);
    }

    /**
     * Handles incoming requests for the /login endpoint which authenticates an
     * existing customer.
     * @param request The request body.
     * @return The customer data with a JWT token in the header.
     */
    @Operation(summary = "Authenticates a customer account")
    @PostMapping("/login")
    public ResponseEntity<CustomerResponse> handleLoginRequest(
            @RequestBody @Valid LoginRequest request) {
        log.info("POST {}", HttpUtil.getCurrentRequestPath());

        Customer customer = customerAuthService.authenticateCustomer(request);
        log.info("Customer signed in (ID: {})", customer.getId());
        String token = jwtUtil.generateToken(customer);

        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(token);
        CustomerResponse response = customerMapper.mapToResponse(customer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(response);
    }
}
