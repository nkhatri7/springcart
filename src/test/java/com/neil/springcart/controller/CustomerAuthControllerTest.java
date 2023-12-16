package com.neil.springcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.repository.CartRepository;
import com.neil.springcart.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CustomerAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void registerRouteShouldCreateCustomer() throws Exception {
        String email = "test@gmail.com";
        RegisterRequest request = new RegisterRequest("name", email,
                "password");
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated());
        assertThat(customerRepository.findAll().size()).isEqualTo(1);
        assertThat(customerRepository.findByEmail(email).isPresent()).isTrue();
    }

    @Test
    void registerRouteShouldCreateCustomerCart() throws Exception {
        RegisterRequest request = new RegisterRequest("name", "email",
                "password");
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());
        assertThat(cartRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void registerRouteShouldReturn400WhenEmailInRequestIsTaken() throws Exception {
        // Given a customer with the email test@gmail.com already exists
        String email = "test@gmail.com";
        saveCustomerToDb(email);
        // When a request is made to the /register route
        // Then the response should have a status code of 400
        RegisterRequest request = new RegisterRequest("name", email,
                "password");
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginRouteShouldReturnCustomerDetailsWithToken() throws Exception {
        // Given a customer account under test@gmail.com exists
        String email = "test@gmail.com";
        String rawPassword = "password";
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        saveCustomerToDb(email, encryptedPassword);

        LoginRequest request = new LoginRequest(email, rawPassword);
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void loginRouteShouldReturn400StatusWhenAccountWithEmailDoesNotExist() throws Exception {
        LoginRequest request = new LoginRequest("test@gmail.com", "password");
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginRouteShouldReturn400StatusWhenPasswordIsIncorrect() throws Exception {
        // Given a customer account under test@gmail.com exists
        String email = "test@gmail.com";
        String encryptedPassword = passwordEncoder.encode("password");
        saveCustomerToDb(email, encryptedPassword);

        LoginRequest request = new LoginRequest(email, "incorrectPassword");
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    private void saveCustomerToDb(String email) {
        Customer customer = buildCustomer(email, "password");
        customerRepository.save(customer);
    }

    private void saveCustomerToDb(String email, String password) {
        Customer customer = buildCustomer(email, password);
        customerRepository.save(customer);
    }

    private Customer buildCustomer(String email, String password) {
        return Customer.builder()
                .name("name")
                .email(email)
                .password(password)
                .build();
    }
}