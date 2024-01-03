package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.OrderLineItemDto;
import com.neil.springcart.dto.OrderSummary;
import com.neil.springcart.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

@Component
public class OrderMapper {
    public Order mapToOrder(Customer customer, Address address) {
        return Order.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .date(new Date())
                .shippingAddress(address)
                .isCancelled(false)
                .build();
    }

    public OrderLineItem mapToOrderLineItem(OrderLineItemDto itemDto,
            Product product, InventoryItem inventoryItem) {
        return OrderLineItem.builder()
                .product(product)
                .size(itemDto.size())
                .inventoryItem(inventoryItem)
                .isReturned(false)
                .build();
    }
}
