package com.neil.springcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neil.springcart.dto.LoginRequest;
import com.neil.springcart.model.Admin;
import com.neil.springcart.repository.AdminRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class InternalAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void loginShouldReturnAdminDetailsWithToken() throws Exception {
        // Given an admin account under admin@springcart.com exists
        String email = "admin@springcart.com";
        String rawPassword = "password";
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        saveAdminToDb(email, encryptedPassword);
        // When a request is made with the email admin@springcart.com and the
        // correct password
        LoginRequest request = new LoginRequest(email, rawPassword);
        String requestJson = objectMapper.writeValueAsString(request);
        // Then a 200 status is returned with the admin details and a JWT token
        // in the header of the response
        mockMvc.perform(MockMvcRequestBuilders.post("/internal/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void loginShouldReturn400StatusWhenAccountWithEmailDoesNotExist() throws Exception {
        // Given an admin account under admin@springcart.com doesn't exist
        LoginRequest request = new LoginRequest("admin@springcart.com",
                "password");
        String requestJson = objectMapper.writeValueAsString(request);
        // Then a 400 status is returned
        mockMvc.perform(MockMvcRequestBuilders.post("/internal/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginShouldReturn400StatusWhenPasswordIsIncorrect() throws Exception {
        // Given an admin account under admin@springcart.com exists
        String email = "admin@springcart.com";
        String encryptedPassword = passwordEncoder.encode("password");
        saveAdminToDb(email, encryptedPassword);
        // When a request is made with the email admin@springcart.com and the
        // incorrect password
        LoginRequest request = new LoginRequest(email, "incorrectPassword");
        String requestJson = objectMapper.writeValueAsString(request);
        // Then a 400 status is returned
        mockMvc.perform(MockMvcRequestBuilders.post("/internal/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    private void saveAdminToDb(String email, String password) {
        Admin admin = buildAdmin(email, password);
        adminRepository.save(admin);
    }

    private Admin buildAdmin(String email, String password) {
        return Admin.builder()
                .email(email)
                .password(password)
                .build();
    }
}