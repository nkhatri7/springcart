package com.neil.springcart.service;

import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.dto.OrderLineItemDto;
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
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;

    public Long createOrder(CreateOrderRequest request) {
        Customer customer = getCustomerById(request.customerId());
        Order order = orderMapper.mapToOrder(customer);
        List<OrderLineItem> items = getOrderLineItems(request.items());

        orderRepository.save(order);
        saveOrderLineItems(items, order);
        log.info("Order (ID: {}) created", order.getId());
        return order.getId();
    }

    private Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Invalid customer ID")
        );
    }

    private void saveOrderLineItems(List<OrderLineItem> items, Order order) {
        items.forEach(item -> item.setOrder(order));
        orderLineItemRepository.saveAll(items);
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
                .mapToObj(i -> createOrderLineItem(item, product, inventory, i))
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
        return inventoryRepository.findInventoryByProductAndSize(
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
                                              List<InventoryItem> inventory,
                                              int index) {
        InventoryItem inventoryItem = inventory.get(index);
        updateInventoryItemStatus(inventoryItem);
        return orderMapper.mapToOrderLineItem(itemDto, product, inventoryItem);
    }

    @Transactional
    private void updateInventoryItemStatus(InventoryItem inventoryItem) {
        inventoryItem.setSold(true);
    }
}
