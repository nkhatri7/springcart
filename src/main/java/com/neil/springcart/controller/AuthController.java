package com.neil.springcart.controller;

import com.neil.springcart.dto.CustomerResponse;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Customer;
import com.neil.springcart.service.AuthService;
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
public class AuthController {
    private final AuthService authService;

    /**
     * Handles incoming requests for the /api/v1/auth/register endpoint which
     * registers a new customer in the system.
     * @param registerRequest The request body.
     * @return The new user data with a JWT token for the customer creating an
     * account.
     * @throws BadRequestException If an account with the email from the request
     * already exists.
     */
    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> handleCustomerRegistration(
            @RequestBody @Valid RegisterRequest registerRequest) {
        log.info("/api/v1/auth/register reached");
        if (authService.isEmailTaken(registerRequest.email().trim())) {
            throw new BadRequestException("Account with email already exists");
        }
        Customer customer = authService.createCustomer(registerRequest);
        String token = authService.generateCustomerToken(customer);
        log.info("Customer JWT token generated");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", "Bearer " + token);
        CustomerResponse customerResponse = authService
                .mapToCustomerResponse(customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(responseHeaders)
                .body(customerResponse);
    }
}
