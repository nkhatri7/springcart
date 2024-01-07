package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.OrderResponse;
import com.neil.springcart.dto.OrderSummary;
import com.neil.springcart.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderMapper {
    private OrderLineItemMapper orderLineItemMapper;

    public OrderSummary mapToSummary(Order order) {
        return OrderSummary.builder()
                .id(order.getId())
                .date(order.getDate())
                .shippingAddress(order.getShippingAddress())
                .items(order.getItems().size())
                .price(order.getTotalAmount())
                .build();
    }

    public OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .date(order.getDate())
                .shippingAddress(order.getShippingAddress())
                .price(order.getTotalAmount())
                .items(orderLineItemMapper.mapOrderLineItemsToResponseList(
                        order.getItems()))
                .build();
    }
}
