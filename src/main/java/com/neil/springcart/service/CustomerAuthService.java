package com.neil.springcart.service;

import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.dto.CustomerResponse;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomerAuthService extends RootAuthService {
    @Autowired
    private final CustomerRepository customerRepository;

    public CustomerAuthService(PasswordEncoder passwordEncoder,
                               JwtUtils jwtUtils,
                               CustomerRepository customerRepository) {
        super(passwordEncoder, jwtUtils);
        this.customerRepository = customerRepository;
    }

    /**
     * Creates a customer in the database with the data from the request.
     * @param request The body of a request from the /register route.
     * @return A Customer object with an ID and data from the request.
     */
    public Customer createCustomer(RegisterRequest request) {
        Customer customer = getCustomerFromRegisterRequest(request);
        // Encrypt received password
        String encryptedPassword = encryptPassword(request.password().trim());
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
     * Searches the database for a customer with the given email.
     * @param email The email of a customer.
     * @return A Customer if one exists with that email, otherwise it is empty.
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
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
