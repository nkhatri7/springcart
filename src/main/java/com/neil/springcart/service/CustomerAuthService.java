package com.neil.springcart.service;

import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Cart;
import com.neil.springcart.repository.CartRepository;
import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.util.PasswordManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class CustomerAuthService {
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final PasswordManager passwordManager;

    /**
     * Creates a customer in the database with the data from the request.
     * @param request The body of a request from the /register route.
     * @return A Customer object with an ID and data from the request.
     */
    public Customer createCustomer(RegisterRequest request) {
        if (isEmailTaken(request.email().trim())) {
            throw new BadRequestException("Account with email already exists");
        }
        String rawPassword = request.password().trim();
        String encryptedPassword = passwordManager.encryptPassword(rawPassword);
        Customer customer = buildCustomer(request, encryptedPassword);
        customerRepository.save(customer);
        createCustomerCart(customer);
        return customer;
    }

    private boolean isEmailTaken(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    private Customer buildCustomer(RegisterRequest request, String password) {
        return Customer.builder()
                .name(request.name().trim())
                .email(request.email().trim())
                .password(password)
                .build();
    }

    private void createCustomerCart(Customer customer) {
        Cart cart = buildCustomerCart(customer);
        cartRepository.save(cart);
    }

    private Cart buildCustomerCart(Customer customer) {
        return Cart.builder()
                .customer(customer)
                .products(new ArrayList<>())
                .build();
    }

    /**
     * Checks if the email and password of the customer are valid.
     * @param request The LoginRequest containing the user email and password.
     * @return The customer data if the login details are valid.
     */
    public Customer authenticateCustomer(LoginRequest request) {
        // Check if customer with email exists
        String email = request.email().trim();
        Customer customer = getCustomerByEmail(email);
        // Check if password is valid
        String password = request.password().trim();
        String customerPassword = customer.getPassword();
        if (!passwordManager.isPasswordValid(password, customerPassword)) {
            throw new BadRequestException("Password is incorrect");
        }
        return customer;
    }

    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() ->
            new BadRequestException("Account with this email doesn't exist")
        );
    }
}
