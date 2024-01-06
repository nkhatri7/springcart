package com.neil.springcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neil.springcart.dto.CartRequest;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.CartRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void getCustomerCartShouldReturnCartDetails() throws Exception {
        // Given a customer makes a request to get their cart details
        Customer customer = saveCustomer();
        Cart cart = saveCart(customer, new ArrayList<>());
        String token = getToken(customer);
        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(token);
        String endpoint = "/api/v1/cart/customer/" + customer.getId();
        // When a request is made
        // Then the cart details are returned
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(cart.getId()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void addProductToCartShouldAddTheProductToTheCart() throws Exception {
        // Given the product with ID 1 is not in the cart with ID 1
        Product product = saveProduct(1L);
        Customer customer = saveCustomer();
        Cart cart = saveCart(customer, new ArrayList<>());
        String token = getToken(customer);
        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(token);
        CartRequest request = new CartRequest(customer.getId(),
                product.getId());
        String requestJson = objectMapper.writeValueAsString(request);
        // When a request is made with cart ID 1 and product ID 1
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(requestJson))
                .andExpect(status().isOk());
        Optional<Cart> optionalCart = cartRepository.findById(cart.getId());
        assertThat(optionalCart.isPresent()).isTrue();
        assertThat(optionalCart.get().getProducts().size()).isEqualTo(1);
    }

    @Test
    void removeProductFromCartShouldRemoveTheProductFromTheCart() throws Exception {
        // Given the product with ID 1 is in the cart with ID 1
        Customer customer = saveCustomer();
        Product product = saveProduct(1L);
        List<Product> cartProducts = new ArrayList<>();
        cartProducts.add(product);
        Cart cart = saveCart(customer, cartProducts);
        String token = getToken(customer);
        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(token);
        CartRequest request = new CartRequest(customer.getId(),
                product.getId());
        String requestJson = objectMapper.writeValueAsString(request);
        // When a request is made with cart ID 1 and product ID 1
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(requestJson))
                .andExpect(status().isOk());
        Optional<Cart> optionalCart = cartRepository.findById(cart.getId());
        assertThat(optionalCart.isPresent()).isTrue();
        assertThat(optionalCart.get().getProducts().size()).isEqualTo(0);
    }

    private Product saveProduct(Long id) {
        Product product = buildProduct(id);
        return productRepository.save(product);
    }

    private Product buildProduct(Long id) {
        return Product.builder()
                .id(id)
                .sku(UUID.randomUUID())
                .brand("brand")
                .name("name")
                .description("description")
                .gender(ProductGender.UNISEX)
                .category(ProductCategory.SPORTSWEAR)
                .price(50)
                .inventory(new ArrayList<>())
                .isActive(true)
                .build();
    }

    private Cart saveCart(Customer customer, List<Product> products) {
        Cart cart = buildCart(customer, products);
        return cartRepository.save(cart);
    }

    private Cart buildCart(Customer customer, List<Product> products) {
        return Cart.builder()
                .products(products)
                .customer(customer)
                .build();
    }

    private String getToken(Customer customer) {
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