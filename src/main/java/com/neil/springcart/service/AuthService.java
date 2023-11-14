package com.neil.springcart.service;

import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.dto.CustomerResponse;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.security.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Creates a customer in the database with the data from the request.
     * @param request The body of a request from the /register route.
     * @return A Customer object with an ID and data from the request.
     */
    public Customer createCustomer(RegisterRequest request) {
        Customer customer = getCustomerFromRegisterRequest(request);
        // Encrypt received password
        String encryptedPassword = passwordEncoder
                .encode(request.password().trim());
        customer.setPassword(encryptedPassword);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created in database");
        return savedCustomer;
    }

    /**
     * Checks if a Customer exists with that email.
     * @param email The email of a customer.
     * @return {@code true} if the email is taken, {@code false} if it is not.
     */
    public boolean isEmailTaken(String email) {
        Optional<Customer> existingCustomer = customerRepository
                .findByEmail(email);
        return existingCustomer.isPresent();
    }

    /**
     * Maps the given Customer object to a CustomerResponse object.
     * @param customer The Customer object to be converted to CustomerResponse.
     * @return A CustomerResponse object with the name and email from the
     * Customer object.
     */
    public CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }

    /**
     * Generates a JWT token for the given Customer
     * @param customer A Customer object
     * @return A JWT token signed with the customer's credentials.
     */
    public String generateCustomerToken(Customer customer) {
        return jwtUtils.generateToken(customer);
    }

    /**
     * Maps the data from the given RegisterRequest object to a Customer object.
     * @param request The body of a request from the /register route.
     * @return A Customer object with the data from the RegisterRequest object.
     */
    private Customer getCustomerFromRegisterRequest(
            RegisterRequest request) {
        return Customer.builder()
                .name(request.name().trim())
                .email(request.email().trim())
                .build();
    }
}
