package com.neil.springcart.service;

import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Cart;
import com.neil.springcart.repository.CartRepository;
import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.util.PasswordManager;
import com.neil.springcart.util.mapper.CustomerRegistrationMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerAuthService {
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final PasswordManager passwordManager;
    private final CustomerRegistrationMapper customerRegistrationMapper;

    /**
     * Creates a customer in the database with the data from the request.
     * @param request The body of a request from the /register route.
     * @return A Customer object with an ID and data from the request.
     * @throws BadRequestException If an account with the email from the request
     * already exists.
     */
    public Customer createCustomer(RegisterRequest request) {
        if (isEmailTaken(request.email().trim())) {
            throw new BadRequestException("Account with email already exists");
        }
        String rawPassword = request.password().trim();
        String encryptedPassword = passwordManager.encryptPassword(rawPassword);
        Customer customer = customerRegistrationMapper.mapToCustomer(request,
                encryptedPassword);
        customerRepository.save(customer);
        createCustomerCart(customer);
        return customer;
    }

    /**
     * Creates a cart in the database for the given customer.
     * @param customer The customer the cart is to be created for.
     */
    private void createCustomerCart(Customer customer) {
        Cart cart = buildCustomerCart(customer);
        cartRepository.save(cart);
    }

    /**
     * Builds a Cart POJO for the given customer.
     * @param customer The customer the cart is to be built for.
     * @return A Cart object linked to the given customer.
     */
    private Cart buildCustomerCart(Customer customer) {
        return Cart.builder()
                .customer(customer)
                .products(new ArrayList<>())
                .build();
    }

    /**
     * Checks if a Customer exists with that email.
     * @param email The email of a customer.
     * @return {@code true} if the email is taken, {@code false} if it is not.
     */
    private boolean isEmailTaken(String email) {
        Optional<Customer> existingCustomer = customerRepository
                .findByEmail(email);
        return existingCustomer.isPresent();
    }

    /**
     * Checks if the email and password of the customer are valid.
     * @param request The LoginRequest containing the user email and password.
     * @return The customer data if the login details are valid.
     * @throws BadRequestException If an account with the email from the request
     * doesn't exist or if the password is invalid.
     */
    public Customer authenticateCustomer(LoginRequest request) {
        // Check if customer with email exists
        String email = request.email().trim();
        Customer customer = getCustomerByEmail(email).orElseThrow(() ->
            new BadRequestException("Account with this email doesn't exist")
        );
        // Check if password is valid
        String password = request.password().trim();
        String customerPassword = customer.getPassword();
        if (!passwordManager.isPasswordValid(password, customerPassword)) {
            throw new BadRequestException("Password is incorrect");
        }
        return customer;
    }

    /**
     * Searches the database for a customer with the given email.
     * @param email The email of a customer.
     * @return A Customer if one exists with that email, otherwise it is empty.
     */
    private Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
