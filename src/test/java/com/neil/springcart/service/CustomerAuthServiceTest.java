package com.neil.springcart.service;

import com.neil.springcart.dto.CustomerResponse;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.security.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerAuthServiceTest {
    private CustomerAuthService customerAuthService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        customerAuthService = new CustomerAuthService(customerRepository,
                passwordEncoder, jwtUtils);
    }

    @AfterEach
    void tearDown() {
        reset(customerRepository, passwordEncoder, jwtUtils);
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
                .isEqualTo(passwordEncoder.encode(password));
    }

    @Test
    void isEmailTaken_itShouldReturnTrueWhenACustomerExistsInTheDatabaseWithTheGivenEmail() {
        // Given a customer account exists with test@gmail.com as the email
        String email = "test@gmail.com";
        Customer customer = createCustomerWithEmail(email);
        given(customerRepository.findByEmail(email))
                .willReturn(Optional.of(customer));
        // When isEmailTaken() is called with test@gmail.com
        boolean isEmailTaken = customerAuthService.isEmailTaken(email);
        // Then it should return true
        assertThat(isEmailTaken).isTrue();
    }

    @Test
    void isEmailTaken_itShouldReturnFalseWhenACustomerDoesNotExistInTheDatabaseWithTheGivenEmail() {
        // Given a customer account doesn't exist with test@gmail.com as the
        // email
        String email = "test@gmail.com";
        given(customerRepository.findByEmail(email))
                .willReturn(Optional.empty());
        // When isEmailTaken() is called with test@gmail.com
        boolean isEmailTaken = customerAuthService.isEmailTaken(email);
        // Then it should return false
        assertThat(isEmailTaken).isFalse();
    }

    @Test
    void mapToCustomerResponse_itShouldHaveTheSameNameAsTheGivenCustomerObject() {
        // Given a Customer object
        String name = "name";
        Customer customer = new Customer(1L, name, "email", "password");
        // When mapToCustomerResponse() is called
        CustomerResponse response = customerAuthService.mapToCustomerResponse(customer);
        // Then the CustomerResponse object should have the same name as the
        // given Customer object
        assertThat(response.name()).isEqualTo(customer.getName());
    }

    @Test
    void mapToCustomerResponse_itShouldHaveTheSameEmailAsTheGivenCustomerObject() {
        // Given a Customer object
        String email = "email";
        Customer customer = new Customer(1L, "name", email, "password");
        // When mapToCustomerResponse() is called
        CustomerResponse response = customerAuthService
                .mapToCustomerResponse(customer);
        // Then the CustomerResponse object should have the same email as the
        // given Customer object
        assertThat(response.email()).isEqualTo(customer.getEmail());
    }

    @Test
    void generateUserToken_itShouldCallGenerateTokenFromJwtUtils() {
        // When generateCustomerToken() is called
        Customer customer = new Customer(1L, "name", "email", "password");
        customerAuthService.generateUserToken(customer);
        // Then JwtUtils' generateToken() is called
        verify(jwtUtils).generateToken(customer);
    }

    private Customer createCustomerWithEmail(String email) {
        return Customer.builder()
                .name("test")
                .email(email)
                .password("password")
                .build();
    }
}