package com.neil.springcart.controller;

import com.neil.springcart.model.Customer;
import com.neil.springcart.model.Product;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.HttpUtil;
import com.neil.springcart.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpUtil httpUtil;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void getAllProductsReturnsOneProductIfThereIsOneActiveProduct() throws Exception {
        // Given there are 2 products and one of them is active
        saveProduct("product 1", false);
        saveProduct("product 2", true);
        String token = getToken();
        HttpHeaders headers = httpUtil.generateAuthorizationHeader(token);
        // When a request is made
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    private void saveProduct(String name, boolean isActive) {
        Product product = buildProduct(name, isActive);
        productRepository.save(product);
    }

    private Product buildProduct(String name, boolean isActive) {
        return Product.builder()
                .brand("brand")
                .name(name)
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.UNISEX)
                .isActive(isActive)
                .inventoryList(new ArrayList<>())
                .build();
    }

    private String getToken() {
        Customer customer = saveCustomer();
        return jwtUtil.generateToken(customer);
    }

    private Customer saveCustomer() {
        Customer customer = buildCustomer();
        return customerRepository.save(customer);
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .name("name")
                .email("test@gmail.com")
                .password("password")
                .build();
    }
}