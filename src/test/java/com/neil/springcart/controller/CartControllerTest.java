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
    @Autowired
    private HttpUtil httpUtil;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void addProductToCartShouldAddTheProductToTheCart() throws Exception {
        // Given the product with ID 1 is not in the cart with ID 1
        Product product = saveProduct(1L);
        Customer customer = saveCustomerWithCart(new ArrayList<>());
        String token = getToken(customer);
        HttpHeaders headers = httpUtil.generateAuthorizationHeader(token);
        CartRequest request = new CartRequest(customer.getId(),
                product.getId());
        String requestJson = objectMapper.writeValueAsString(request);
        // When a request is made with cart ID 1 and product ID 1
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(requestJson))
                .andExpect(status().isOk());
        Optional<Cart> cart = cartRepository.findById(customer.getId());
        assertThat(cart.isPresent()).isTrue();
        assertThat(cart.get().getProducts().size()).isEqualTo(1);
    }

    @Test
    void removeProductFromCartShouldRemoveTheProductFromTheCart() throws Exception {
        // Given the product with ID 1 is in the cart with ID 1
        Product product = saveProduct(1L);
        List<Product> cartProducts = new ArrayList<>();
        cartProducts.add(product);
        Customer customer = saveCustomerWithCart(cartProducts);
        String token = getToken(customer);
        HttpHeaders headers = httpUtil.generateAuthorizationHeader(token);
        CartRequest request = new CartRequest(customer.getId(),
                product.getId());
        String requestJson = objectMapper.writeValueAsString(request);
        // When a request is made with cart ID 1 and product ID 1
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(requestJson))
                .andExpect(status().isOk());
        Optional<Cart> cart = cartRepository.findById(customer.getId());
        assertThat(cart.isPresent()).isTrue();
        assertThat(cart.get().getProducts().size()).isEqualTo(0);
    }

    private Customer saveCustomerWithCart(List<Product> products) {
        Customer customer = buildCustomer();
        Cart cart = buildCart(customer, products);
        customer.setCart(cart);
        saveCustomer(customer);
        cartRepository.save(cart);
        return customer;
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

    private Cart buildCart(Customer customer, List<Product> products) {
        return Cart.builder()
                .id(customer.getId())
                .products(products)
                .customer(customer)
                .build();
    }

    private String getToken(Customer customer) {
        return jwtUtil.generateToken(customer);
    }

    private Customer saveCustomer(Customer customer) {
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