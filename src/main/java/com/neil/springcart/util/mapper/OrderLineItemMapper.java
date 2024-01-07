package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.OrderLineItemResponse;
import com.neil.springcart.model.OrderLineItem;
import com.neil.springcart.model.Product;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderLineItemMapper {
    public List<OrderLineItemResponse> mapOrderLineItemsToResponseList(
            List<OrderLineItem> items) {
        // Need to group OrderLineItems that have the same product and size
        Map<String, OrderLineItemResponse> groupedItems = new HashMap<>();

        for (OrderLineItem item : items) {
            // Create key for item to group items with same product and size
            String key = getOrderLineItemKey(item);
            OrderLineItemResponse existingItem = groupedItems.get(key);
            if (existingItem != null) {
                // Increment quantity on existing item
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            } else {
                groupedItems.put(key, mapToOrderLineItemResponse(item));
            }
        }

        return groupedItems.values().stream().toList();
    }

    private String getOrderLineItemKey(OrderLineItem item) {
        // Key will be combination of product ID and size for grouping
        Product product = item.getProduct();
        return product.getId() + "-" + item.getSize();
    }

    private OrderLineItemResponse mapToOrderLineItemResponse(
            OrderLineItem item) {
        Product product = item.getProduct();
        return OrderLineItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .brand(product.getBrand())
                .name(product.getName())
                .size(item.getSize())
                .price(product.getPrice())
                // Initialise quantity as 1
                .quantity(1)
                .build();
    }
}
