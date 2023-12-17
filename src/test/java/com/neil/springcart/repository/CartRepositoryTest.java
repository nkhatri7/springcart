package com.neil.springcart.repository;

import com.neil.springcart.model.Cart;
import com.neil.springcart.model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    void tearDown() {
        cartRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void findByCustomerIdShouldReturnACartIfTheCustomerIdIsValid() {
        // Given a customer with ID 1 exists
        Customer customer = saveCustomer();
        saveCart(customer);
        // When findByCustomerId() is called
        Optional<Cart> cart = cartRepository.findByCustomerId(
                customer.getId());
        // Then the result is a cart object
        assertThat(cart).isNotEmpty();
    }

    @Test
    void findByCustomerIdShouldReturnAnEmptyObjectIfTheCustomerIdIsInvalid() {
        // Given a customer with ID 5 does not exist
        // When findByCustomerId() is called
        Optional<Cart> cart = cartRepository.findByCustomerId(5L);
        // Then the result is empty
        assertThat(cart).isEmpty();
    }

    private Cart saveCart(Customer customer) {
        Cart cart = buildCart(customer);
        return cartRepository.save(cart);
    }

    private Cart buildCart(Customer customer) {
        return Cart.builder()
                .customer(customer)
                .products(new ArrayList<>())
                .build();
    }

    private Customer saveCustomer() {
        Customer customer = buildCustomer();
        return customerRepository.save(customer);
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .name("name")
                .email("email")
                .password("password")
                .build();
    }
}