package com.neil.springcart.repository;

import com.neil.springcart.model.Customer;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void findByEmail_itShouldReturnCustomerIfCustomerWithEmailExists() {
        // Given a customer with the email test@gmail.com exists
        String email = "test@gmail.com";
        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail(email);
        customer.setPassword("password");
        customerRepository.save(customer);
        // When findByEmail is called
        Optional<Customer> customerInDb = customerRepository
                .findByEmail(customer.getEmail());
        // Then result is a Customer object
        assertThat(customerInDb).isNotEmpty();
    }

    @Test
    void findByEmail_itShouldReturnAnEmptyObjectIfCustomerWithEmailDoesNotExist() {
        // Given a user with the email test@gmail.com doesn't exist
        String email = "test@gmail.com";
        // When findByEmail is called
        Optional<Customer> customerInDb = customerRepository.findByEmail(email);
        // Then the result is empty
        assertThat(customerInDb).isEmpty();
    }
}