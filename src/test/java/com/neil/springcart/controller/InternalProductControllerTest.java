package com.neil.springcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.AdminRepository;
import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.repository.InventoryRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.security.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class InternalProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
        customerRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void handleNewProductCreatesProductInDatabase() throws Exception {
        // When a request is coming from an admin and their JWT token
        Admin admin = createAdmin();
        String token = generateUserToken(admin);
        NewProductRequest request = generateNewProductRequest();
        String requestJson = objectMapper.writeValueAsString(request);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 201 status is returned with the product, and it's inventory
        // items being saved in the database
        mockMvc.perform(MockMvcRequestBuilders.post("/internal/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(requestHeaders)
                        .content(requestJson))
                .andExpect(status().isCreated());
        assertThat(productRepository.findAll().size()).isEqualTo(1);
        assertThat(inventoryRepository.findAll().size())
                .isEqualTo(request.inventory().size());
    }

    @Test
    void handleNewProductReturns403StatusWhenANonAdminMakesTheRequest()
            throws Exception {
        // When a request is coming from a customer and their JWT token
        Customer customer = createCustomer();
        String token = generateUserToken(customer);
        NewProductRequest request = generateNewProductRequest();
        String requestJson = objectMapper.writeValueAsString(request);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 403 status is returned
        mockMvc.perform(MockMvcRequestBuilders.post("/internal/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(requestHeaders)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }

    private String generateUserToken(UserDetails user) {
        return jwtUtils.generateToken(user);
    }

    private Admin createAdmin() {
        Admin admin = Admin.builder()
                .email("admin@springcart.com")
                .password("password")
                .build();
        return adminRepository.save(admin);
    }

    private Customer createCustomer() {
        Customer customer = Customer.builder()
                .name("name")
                .email("notanadmin@springcart.com")
                .password("password")
                .build();
        return customerRepository.save(customer);
    }

    private NewProductRequest generateNewProductRequest() {
        List<InventoryDto> inventoryList = List.of(
                new InventoryDto(ProductSize.S, 5),
                new InventoryDto(ProductSize.L, 10)
        );
        return NewProductRequest.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .inventory(inventoryList)
                .build();
    }
}