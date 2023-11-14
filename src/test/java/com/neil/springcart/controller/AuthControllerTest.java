package com.neil.springcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import com.neil.springcart.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;

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
    void registerRouteShouldReturn400WhenEmailInRequestIsTaken() throws Exception {
        // Given a customer with the email test@gmail.com already exists
        String email = "test@gmail.com";
        Customer customer = buildCustomerWithEmail(email);
        customerRepository.save(customer);
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

    private Customer buildCustomerWithEmail(String email) {
        return Customer.builder()
                .name("name")
                .email(email)
                .password("password")
                .build();
    }
}