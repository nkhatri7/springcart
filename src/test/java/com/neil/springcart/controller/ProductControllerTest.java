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
        // When a request is made, then one product is returned
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getProductsByGenderReturnsOneProductIfThereIsOneProductForTheGivenGender() throws Exception {
        // Given there are 2 products and one of them is MALE
        ProductGender gender = ProductGender.MALE;
        saveProduct("male product", gender);
        saveProduct("female product", ProductGender.FEMALE);
        String token = getToken();
        HttpHeaders headers = httpUtil.generateAuthorizationHeader(token);
        // When a request is made with the gender being specified as MALE
        // Then one product is returned
        String endpoint = "/api/v1/products?gender=" + gender;
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getProductsByGenderAndCategoryReturnsOneProductIfThereIsOneProductForTheGivenGenderAndCategory() throws Exception {
        // Given there are 2 products and one of them is MALE and SPORTSWEAR
        ProductGender gender = ProductGender.MALE;
        ProductCategory category = ProductCategory.SPORTSWEAR;
        saveProduct("male product", gender, category);
        saveProduct("female product", ProductGender.FEMALE, category);
        String token = getToken();
        HttpHeaders headers = httpUtil.generateAuthorizationHeader(token);
        // When a request is made with the gender being specified as MALE and
        // the category being specified as SPORTSWEAR
        // Then one product is returned
        String endpoint = "/api/v1/products?gender=" + gender + "&category="
                + category;
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getProductReturnsProductDetailsIfAProductWithTheIdExists() throws Exception {
        // Given a product with ID 1 exists
        Product product = saveProduct("product", true);
        String token = getToken();
        HttpHeaders headers = httpUtil.generateAuthorizationHeader(token);
        // When a request is made with the product ID
        // Then the product details are returned
        String endpoint = "/api/v1/products/" + product.getId();
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value(product.getBrand()))
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    private Product saveProduct(String name, ProductGender gender,
                             ProductCategory category) {
        Product product = buildProduct(name, gender, category, true);
        return productRepository.save(product);
    }

    private Product saveProduct(String name, ProductGender gender) {
        Product product = buildProduct(name, gender, ProductCategory.SPORTSWEAR,
                true);
        return productRepository.save(product);
    }

    private Product saveProduct(String name, boolean isActive) {
        Product product = buildProduct(name, ProductGender.UNISEX,
                ProductCategory.SPORTSWEAR, isActive);
        return productRepository.save(product);
    }

    private Product buildProduct(String name, ProductGender gender,
                                 ProductCategory category, boolean isActive) {
        return Product.builder()
                .brand("brand")
                .name(name)
                .description("description")
                .category(category)
                .gender(gender)
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