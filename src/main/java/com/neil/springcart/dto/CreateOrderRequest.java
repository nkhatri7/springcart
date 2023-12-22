package com.neil.springcart.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "Missing customer ID") Long customerId,
        List<OrderLineItemDto> items) {}
