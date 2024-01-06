package com.neil.springcart.service;

import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.dto.OrderLineItemDto;
import com.neil.springcart.dto.OrderSummary;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.*;
import com.neil.springcart.util.mapper.OrderMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        Order order = orderMapper.mapToOrder(customer,
                request.shippingAddress());
        List<OrderLineItem> items = getOrderLineItems(request.items());

        orderRepository.save(order);
        saveOrderLineItems(items, order);
        log.info("Order (ID: {}) created", order.getId());
        return getOrderSummary(order);
    }

    private Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Invalid customer ID")
        );
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
                .mapToObj(i -> createOrderLineItem(item, product,
                        inventory.get(i)))
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

    private OrderLineItem createOrderLineItem(OrderLineItemDto itemDto,
                                              Product product,
                                              InventoryItem inventoryItem) {
        updateInventoryItemStatus(inventoryItem);
        return orderMapper.mapToOrderLineItem(itemDto, product, inventoryItem);
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

    private OrderSummary getOrderSummary(Order order) {
        double orderPrice = calculateOrderPrice(order.getItems());
        return buildOrderSummary(order, orderPrice);
    }

    private double calculateOrderPrice(List<OrderLineItem> orderLineItems) {
        return orderLineItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice())
                .sum();
    }

    private OrderSummary buildOrderSummary(Order order, double orderPrice) {
        return OrderSummary.builder()
                .id(order.getId())
                .date(order.getDate())
                .shippingAddress(order.getShippingAddress())
                .items(order.getItems().size())
                .price(orderPrice)
                .build();
    }

    /**
     * Gets the orders for the customer with the given ID.
     * @param customerId The customer ID.
     * @return A list of order summaries for the customer with the given ID.
     */
    public List<OrderSummary> getCustomerOrders(Long customerId) {
        List<Order> customerOrders = orderRepository.findAllByCustomerId(
                customerId);
        return customerOrders.stream()
                .map(this::getOrderSummary)
                .toList();
    }
}
