package com.neil.springcart.dto;

import com.neil.springcart.model.Address;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "Missing customer ID")
        Long customerId,
        @NotNull(message = "Missing order line items")
        List<OrderLineItemDto> items,
        @NotNull(message = "Missing shipping address")
        Address shippingAddress
) {}
