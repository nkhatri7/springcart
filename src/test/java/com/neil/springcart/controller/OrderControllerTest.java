package com.neil.springcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.dto.OrderLineItemDto;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.*;
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
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderLineItemRepository orderLineItemRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        orderLineItemRepository.deleteAll();
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void createOrderCreatesAnOrderInTheDatabase() throws Exception {
        // Given there is a product with 2 inventory items in stock for size S
        Customer customer = saveCustomer();
        Product product = saveProduct();
        saveInventory(product, ProductSize.S, 2);

        // When a request is made for 2 items of the product in size S
        List<OrderLineItemDto> orderLineItemDtos = List.of(
                new OrderLineItemDto(product.getId(), ProductSize.S, 2)
        );
        CreateOrderRequest request = buildCreateOrderRequest(customer.getId(),
                orderLineItemDtos);
        String requestJson = objectMapper.writeValueAsString(request);
        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(
                getCustomerToken(customer));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .headers(headers)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Then an order is created and 2 items are included in the order
        assertThat(orderRepository.findAll().size()).isEqualTo(1);
        assertThat(orderLineItemRepository.findAll().size())
                .isEqualTo(2);
    }

    @Test
    void getCustomerOrdersReturnsOneItemIfACustomerHasMadeOneOrder() throws Exception {
        // Given a customer has made 1 order
        Customer customer = saveCustomer();
        Product product = saveProduct();
        saveInventory(product, ProductSize.S, 1);
        saveOrders(customer, 1);

        // When a request is made to get the customer's orders
        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(
                getCustomerToken(customer));
        String requestUrl = "/api/v1/orders/customer/" + customer.getId();
        mockMvc.perform(MockMvcRequestBuilders.get(requestUrl).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getCustomerOrdersReturnsThreeItemsIfACustomerHasMadeThreeOrders() throws Exception {
        // Given a customer has made 3 orders
        Customer customer = saveCustomer();
        Product product = saveProduct();
        saveInventory(product, ProductSize.S, 3);
        saveOrders(customer, 3);

        // When a request is made to get the customer's orders
        HttpHeaders headers = HttpUtil.generateAuthorizationHeader(
                getCustomerToken(customer));
        String requestUrl = "/api/v1/orders/customer/" + customer.getId();
        mockMvc.perform(MockMvcRequestBuilders.get(requestUrl).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    private Customer saveCustomer() {
        return customerRepository.save(buildCustomer());
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .name("name")
                .email("test@gmail.com")
                .password("password")
                .build();
    }

    private String getCustomerToken(Customer customer) {
        return jwtUtil.generateToken(customer);
    }

    private void saveOrders(Customer customer, int numOrders) {
        for (int i = 0; i < numOrders; i++) {
            orderRepository.save(buildOrder(customer));
        }
    }

    private Order buildOrder(Customer customer) {
        return Order.builder()
                .customer(customer)
                .shippingAddress(buildAddress())
                .date(new Date())
                .items(new ArrayList<>())
                .isCancelled(false)
                .build();
    }

    private Product saveProduct() {
        return productRepository.save(buildProduct());
    }

    private Product buildProduct() {
        return Product.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.UNISEX)
                .price(50)
                .inventory(new ArrayList<>())
                .isActive(true)
                .build();
    }

    private void saveInventory(Product product, ProductSize size,
                               int quantity) {
        List<InventoryItem> inventory = IntStream.range(0, quantity)
                .mapToObj(i -> buildInventoryItem(product, size))
                .toList();
        inventoryItemRepository.saveAll(inventory);
    }

    private InventoryItem buildInventoryItem(Product product,
                                             ProductSize size) {
        return InventoryItem.builder()
                .product(product)
                .size(size)
                .isSold(false)
                .build();
    }

    private CreateOrderRequest buildCreateOrderRequest(Long customerId,
            List<OrderLineItemDto> items) {
        return new CreateOrderRequest(customerId, items, buildAddress());
    }

    private Address buildAddress() {
        return Address.builder()
                .streetAddress("123 test st")
                .suburb("suburb")
                .state(AuState.NSW)
                .postcode(2000)
                .country("Australia")
                .build();
    }
}