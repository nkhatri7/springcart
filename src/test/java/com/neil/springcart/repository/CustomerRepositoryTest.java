package com.neil.springcart.repository;

import com.neil.springcart.model.Customer;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
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
        Customer customer = createCustomerWithEmail(email);
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

    private Customer createCustomerWithEmail(String email) {
        return Customer.builder()
                .name("test")
                .email(email)
                .password("password")
                .build();
    }
}