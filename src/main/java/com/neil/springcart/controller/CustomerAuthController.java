package com.neil.springcart.controller;

import com.neil.springcart.dto.CustomerResponse;
import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Customer;
import com.neil.springcart.service.CustomerAuthService;
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

    /**
     * Handles incoming requests for the /register endpoint which registers a
     * new customer in the system.
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
        if (customerAuthService.isEmailTaken(registerRequest.email().trim())) {
            throw new BadRequestException("Account with email already exists");
        }
        Customer customer = customerAuthService.createCustomer(registerRequest);
        String token = customerAuthService.generateUserToken(customer);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        CustomerResponse customerResponse = customerAuthService
                .mapToCustomerResponse(customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(responseHeaders)
                .body(customerResponse);
    }

    /**
     * Handles incoming requests for the /login endpoint which authenticates an
     * existing customer.
     * @param loginRequest The request body.
     * @return The customer data with a JWT token in the header.
     * @throws BadRequestException If an account with the email from the request
     * doesn't exist.
     * @throws BadRequestException If the password from the request is
     * incorrect.
     */
    @PostMapping("/login")
    public ResponseEntity<CustomerResponse> handleLoginRequest(
            @RequestBody @Valid LoginRequest loginRequest) {
        log.info("/api/v1/auth/login reached");
        // Check if account with email exists
        Customer customer = customerAuthService
                .getCustomerByEmail(loginRequest.email())
                .orElseThrow(() -> new BadRequestException(
                        "Account with this email doesn't exist"));
        if (!customerAuthService.isPasswordValid(loginRequest.password(),
                customer.getPassword())) {
            throw new BadRequestException("Password is incorrect");
        }
        String token = customerAuthService.generateUserToken(customer);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        CustomerResponse response = customerAuthService
                .mapToCustomerResponse(customer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(response);
    }
}
