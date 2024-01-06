package com.neil.springcart.repository;

import com.neil.springcart.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderLineItemRepositoryTest {
    @Autowired
    private OrderLineItemRepository orderLineItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    void tearDown() {
        orderLineItemRepository.deleteAll();
        orderRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void findAllByOrderIdShouldReturnOneItemIfThereIsOneItemWithThatOrderId() {
        // Given there is one item in the order
        Product product = productRepository.save(buildProduct());
        Order order = orderRepository.save(buildOrder());
        orderLineItemRepository.save(buildOrderLineItem(order, product));
        // When findAllByOrderId() is called
        List<OrderLineItem> orderLineItems = orderLineItemRepository
                .findAllByOrderId(order.getId());
        // Then one OrderLineItem is returned
        assertThat(orderLineItems.size()).isEqualTo(1);
    }

    @Test
    void findAllByOrderIdShouldReturnTwoItemsIfThereAreTwoItemsWithThatOrderId() {
        // Given there are two items in the order
        Product product = productRepository.save(buildProduct());
        Order order = orderRepository.save(buildOrder());
        orderLineItemRepository.save(buildOrderLineItem(order, product));
        orderLineItemRepository.save(buildOrderLineItem(order, product));
        // When findAllByOrderId() is called
        List<OrderLineItem> orderLineItems = orderLineItemRepository
                .findAllByOrderId(order.getId());
        // Then two OrderLineItems are returned
        assertThat(orderLineItems.size()).isEqualTo(2);
    }

    private Order buildOrder() {
        Customer customer = customerRepository.save(buildCustomer());
        return Order.builder()
                .customer(customer)
                .date(new Date())
                .shippingAddress(buildAddress())
                .items(new ArrayList<>())
                .isCancelled(false)
                .build();
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .name("name")
                .email("email")
                .password("password")
                .cart(new Cart())
                .build();
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

    private OrderLineItem buildOrderLineItem(Order order, Product product) {
        InventoryItem inventoryItem = inventoryItemRepository.save(
                buildInventoryItem(ProductSize.S, product));
        return OrderLineItem.builder()
                .product(product)
                .size(inventoryItem.getSize())
                .order(order)
                .inventoryItem(inventoryItem)
                .isReturned(false)
                .build();
    }

    private Product buildProduct() {
        return Product.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .price(50)
                .isActive(true)
                .build();
    }

    private InventoryItem buildInventoryItem(ProductSize size,
                                             Product product) {
        return InventoryItem.builder()
                .size(size)
                .product(product)
                .isSold(false)
                .build();
    }
}