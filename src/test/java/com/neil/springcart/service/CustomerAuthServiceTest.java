package com.neil.springcart.service;

import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.repository.CartRepository;
import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.util.PasswordManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerAuthServiceTest {
    private CustomerAuthService customerAuthService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private PasswordManager passwordManager;

    @BeforeEach
    void setUp() {
        customerAuthService = new CustomerAuthService(customerRepository,
                cartRepository, passwordManager);
    }

    @AfterEach
    void tearDown() {
        reset(customerRepository, cartRepository, passwordManager);
    }

    @Test
    void createCustomer_itShouldCallTheRepositorySaveMethodWithACustomerObject() {
        // Given a request body of a name, email and password
        RegisterRequest request = new RegisterRequest("name", "email",
                "password");
        // When createCustomer() is called
        customerAuthService.createCustomer(request);
        // Then CustomerRepository's save() method is called with a customer
        // object
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_itShouldCreateACustomerWithTheSameNameFromTheRequest() {
        // Given a request body of a name, email and password
        String name = "name";
        RegisterRequest request = new RegisterRequest(name, "email",
                "password");
        // When createCustomer() is called
        customerAuthService.createCustomer(request);
        // Then the Customer object is created with the same name from the
        // request
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor
                .forClass(Customer.class);
        verify(customerRepository).save(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(name);
    }

    @Test
    void createCustomer_itShouldCreateACustomerWithTheSameEmailFromTheRequest() {
        // Given a request body of a name, email and password
        String email = "email";
        RegisterRequest request = new RegisterRequest("name", email,
                "password");
        // When createCustomer() is called
        customerAuthService.createCustomer(request);
        // Then the Customer object is created with the same email from the
        // request
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor
                .forClass(Customer.class);
        verify(customerRepository).save(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
    }

    @Test
    void createCustomer_itShouldCreateACustomerWithAnEncryptedVersionOfThePasswordFromTheRequest() {
        // Given a request body of a name, email and password
        String password = "password";
        RegisterRequest request = new RegisterRequest("name", "email",
                password);
        // When createCustomer() is called
        customerAuthService.createCustomer(request);
        // Then the Customer object has an encrypted version of the password
        // from the request
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor
                .forClass(Customer.class);
        verify(customerRepository).save(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getPassword()).isNotEqualTo(password);
        assertThat(capturedCustomer.getPassword())
                .isEqualTo(passwordManager.encryptPassword(password));
    }

    @Test
    void createCustomer_itShouldCreateACart() {
        // Given a request body of a name, email and password
        RegisterRequest request = new RegisterRequest("name", "email",
                "password");
        // When createCustomer() is called
        customerAuthService.createCustomer(request);
        // Then a cart is created
        verify(cartRepository, times(1)).save(any());
    }
}