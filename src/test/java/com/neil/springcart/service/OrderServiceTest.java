package com.neil.springcart.service;

import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.dto.OrderLineItemDto;
import com.neil.springcart.dto.OrderResponse;
import com.neil.springcart.dto.OrderSummary;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.*;
import com.neil.springcart.util.mapper.OrderLineItemMapper;
import com.neil.springcart.util.mapper.OrderMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderLineItemRepository orderLineItemRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @BeforeEach
    void setUp() {
        OrderLineItemMapper orderLineItemMapper = new OrderLineItemMapper();
        OrderMapper orderMapper = new OrderMapper(orderLineItemMapper);
        orderService = new OrderService(orderRepository,
                orderLineItemRepository, customerRepository, productRepository,
                inventoryItemRepository, orderMapper);
    }

    @AfterEach
    void tearDown() {
        reset(orderRepository, orderLineItemRepository, customerRepository,
                productRepository, inventoryItemRepository);
    }

    @Test
    void createOrderCreatesAnOrderInTheDatabase() {
        // Given a create order request comes in
        given(customerRepository.findById(1L))
                .willReturn(Optional.of(buildCustomer()));
        Product product = buildProduct();
        ProductSize size = ProductSize.S;
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        List<InventoryItem> productInventory = List.of(
                buildInventoryItem(size, product)
        );
        given(inventoryItemRepository.findAllByProductIdAndSize(
                product.getId(), size))
                .willReturn(productInventory);
        List<OrderLineItemDto> orderItems = List.of(
                new OrderLineItemDto(product.getId(), size, 1)
        );

        // When createOrder() is called
        orderService.createOrder(buildCreateOrderRequest(orderItems));

        // Then an order is saved
        verify(orderRepository).save(any());
    }

    @Test
    void createOrderThrowsBadRequestExceptionWhenCustomerIdIsInvalid() {
        // Given a create order request comes in with an invalid customer ID
        given(customerRepository.findById(1L)).willReturn(Optional.empty());
        // When createOrder() is called
        // Then a BadRequestException is thrown
        assertThrows(BadRequestException.class, () -> {
            orderService.createOrder(buildCreateOrderRequest(
                    new ArrayList<>()));
        });
    }

    @Test
    void createOrderCreatesMultipleOrderLineItemsForSameProductIfQuantityOfOrderLineItemDtoIsMoreThanOne() {
        // Given a create order request comes in with an OrderLineItemDto having
        // a quantity of 2
        given(customerRepository.findById(1L))
                .willReturn(Optional.of(buildCustomer()));
        Product product = buildProduct();
        ProductSize size = ProductSize.S;
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        List<InventoryItem> productInventory = List.of(
                buildInventoryItem(size, product),
                buildInventoryItem(size, product)
        );
        given(inventoryItemRepository.findAllByProductIdAndSize(
                product.getId(), size))
                .willReturn(productInventory);
        List<OrderLineItemDto> orderItems = List.of(
                new OrderLineItemDto(product.getId(), size, 2)
        );

        // When createOrder() is called
        orderService.createOrder(buildCreateOrderRequest(orderItems));

        // Then 2 order line items are saved
        ArgumentCaptor<List<OrderLineItem>> argumentCaptor = ArgumentCaptor
                .forClass(List.class);
        verify(orderLineItemRepository).saveAll(argumentCaptor.capture());
        List<OrderLineItem> orderLineItems = argumentCaptor.getValue();
        assertThat(orderLineItems.size()).isEqualTo(2);
    }

    @Test
    void createOrderThrowsBadRequestExceptionWhenThereIsNotEnoughStockForOrderLineItem() {
        // Given a create order request comes in with an OrderLineItemDto having
        // a quantity of 2 but the product only having 1 item of inventory for
        // the order line item
        given(customerRepository.findById(1L))
                .willReturn(Optional.of(buildCustomer()));
        Product product = buildProduct();
        ProductSize size = ProductSize.S;
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        List<InventoryItem> productInventory = List.of(
                buildInventoryItem(size, product)
        );
        given(inventoryItemRepository.findAllByProductIdAndSize(
                product.getId(), size))
                .willReturn(productInventory);
        List<OrderLineItemDto> orderItems = List.of(
                new OrderLineItemDto(product.getId(), size, 2)
        );

        // When createOrder() is called
        // Then a BadRequestException is thrown for not enough stock
        assertThrows(BadRequestException.class, () -> {
            orderService.createOrder(buildCreateOrderRequest(orderItems));
        });
    }

    @Test
    void getCustomerOrdersReturnsAnEmptyListIfTheCustomerHasNoOrders() {
        // Given a customer has no orders
        Long customerId = 1L;
        given(orderRepository.findAllByCustomerId(customerId))
                .willReturn(new ArrayList<>());
        // When getCustomerOrders() is called
        List<OrderSummary> customerOrders = orderService.getCustomerOrders(
                customerId);
        // Then an empty list is returned
        assertThat(customerOrders.isEmpty()).isTrue();
    }

    @Test
    void getCustomerOrdersReturnsTwoOrdersIfTheCustomerHasTwoOrders() {
        // Given a customer has 2 orders
        Long customerId = 1L;
        List<Order> orders = List.of(buildOrder(), buildOrder());
        given(orderRepository.findAllByCustomerId(customerId))
                .willReturn(orders);
        // When getCustomerOrders() is called
        List<OrderSummary> customerOrders = orderService.getCustomerOrders(
                customerId);
        // Then 2 orders are returned
        assertThat(customerOrders.size()).isEqualTo(2);
    }

    @Test
    void getOrderDetailsThrowsBadRequestExceptionIfOrderIdIsInvalid() {
        // Given an order with ID 1 doesn't exist
        Long orderId = 1L;
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());
        // When getOrderDetails() is called
        // Then a BadRequestException is thrown
        assertThrows(BadRequestException.class, () -> {
            orderService.getOrderDetails(orderId);
        });
    }

    @Test
    void getOrderDetailsShouldReturnTheOrderDetailsIfItExists() {
        // Given an order with ID 1 exists
        Order order = buildOrder();
        given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));
        // When getOrderDetails() is called
        OrderResponse orderResponse = orderService.getOrderDetails(
                order.getId());
        // Then the order details are returned
        assertThat(orderResponse).isNotNull();
    }

    private CreateOrderRequest buildCreateOrderRequest(
            List<OrderLineItemDto> items) {
        return new CreateOrderRequest(1L, items, buildAddress());
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
                .id(1L)
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
                .id(1L)
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