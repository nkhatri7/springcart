package com.neil.springcart.service;

import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.dto.OrderLineItemDto;
import com.neil.springcart.dto.OrderResponse;
import com.neil.springcart.dto.OrderSummary;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.*;
import com.neil.springcart.util.mapper.OrderMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final OrderMapper orderMapper;

    /**
     * Creates an order in the database with the data from the request.
     * @param request An object containing the customer ID, items to be ordered,
     *                and the shipping address.
     * @return An order summary for the newly created order.
     */
    public OrderSummary createOrder(CreateOrderRequest request) {
        Customer customer = getCustomerById(request.customerId());
        Order order = buildOrder(customer, request.shippingAddress());
        List<OrderLineItem> items = getOrderLineItems(request.items());

        orderRepository.save(order);
        saveOrderLineItems(items, order);
        log.info("Order (ID: {}) created", order.getId());
        return orderMapper.mapToSummary(order);
    }

    private Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Invalid customer ID")
        );
    }

    private Order buildOrder(Customer customer, Address shippingAddress) {
        return Order.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .date(new Date())
                .shippingAddress(shippingAddress)
                .isCancelled(false)
                .build();
    }

    private List<OrderLineItem> getOrderLineItems(
            List<OrderLineItemDto> items) {
        return items.stream()
                .map(this::processOrderLineItem)
                .flatMap(List::stream)
                .toList();
    }

    private List<OrderLineItem> processOrderLineItem(OrderLineItemDto item) {
        Product product = getActiveProductById(item.productId());
        List<InventoryItem> inventory = getOrderItemInventory(item);

        validateStockAvailability(item, inventory);

        return IntStream.range(0, item.quantity())
                .mapToObj(i -> createOrderLineItem(product, inventory.get(i)))
                .toList();
    }

    private Product getActiveProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Invalid product ID")
        );
        if (!product.isActive()) {
            throw new BadRequestException("Product is inactive");
        }
        return product;
    }

    private List<InventoryItem> getOrderItemInventory(OrderLineItemDto item) {
        return inventoryItemRepository.findAllByProductIdAndSize(
                item.productId(), item.size());
    }

    private void validateStockAvailability(OrderLineItemDto item,
                                           List<InventoryItem> inventory) {
        if (inventory.size() < item.quantity()) {
            throw new BadRequestException("Not enough stock");
        }
    }

    private OrderLineItem createOrderLineItem(Product product,
                                              InventoryItem inventoryItem) {
        updateInventoryItemStatus(inventoryItem);
        return OrderLineItem.builder()
                .product(product)
                .size(inventoryItem.getSize())
                .inventoryItem(inventoryItem)
                .isReturned(false)
                .build();
    }

    @Transactional
    private void updateInventoryItemStatus(InventoryItem inventoryItem) {
        inventoryItem.setSold(true);
    }

    private void saveOrderLineItems(List<OrderLineItem> items, Order order) {
        items.forEach(item -> item.setOrder(order));
        orderLineItemRepository.saveAll(items);
        order.setItems(items);
    }

    /**
     * Gets the orders for the customer with the given ID.
     * @param customerId The customer ID.
     * @return A list of order summaries for the customer with the given ID.
     */
    public List<OrderSummary> getCustomerOrders(Long customerId) {
        List<Order> customerOrders = orderRepository.findAllByCustomerId(
                customerId);
        log.info("Orders retrieved from database for customer (ID: {})",
                customerId);
        return customerOrders.stream()
                .map(orderMapper::mapToSummary)
                .toList();
    }

    /**
     * Gets the details of the order with the given ID.
     * @param id The ID of the order.
     * @return The order details.
     */
    public OrderResponse getOrderDetails(Long id) {
        Order order = getOrderById(id);
        log.info("Order (ID: {}) retrieved from database", order.getId());
        return orderMapper.mapToResponse(order);
    }

    private Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() ->
            new BadRequestException("Order with ID " + id + " does not exist")
        );
    }
}
