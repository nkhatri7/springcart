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
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void findAllByCustomerIdShouldReturnOneOrderIfACustomerHasOneOrder() {
        // Given a customer has only made one order
        Customer customer = customerRepository.save(buildCustomer());
        saveOrders(customer, 1);
        // When findAllByCustomerId() is called
        List<Order> customerOrders = orderRepository.findAllByCustomerId(
                customer.getId());
        // Then one Order is returned
        assertThat(customerOrders.size()).isEqualTo(1);
    }

    @Test
    void findAllByCustomerIdShouldReturnThreeOrdersIfACustomerHasThreeOrders() {
        // Given a customer has made 3 orders
        Customer customer = customerRepository.save(buildCustomer());
        saveOrders(customer, 3);
        // When findAllByCustomerId() is called
        List<Order> customerOrders = orderRepository.findAllByCustomerId(
                customer.getId());
        // Then one Order is returned
        assertThat(customerOrders.size()).isEqualTo(3);
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .name("name")
                .email("email")
                .password("password")
                .cart(new Cart())
                .build();
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