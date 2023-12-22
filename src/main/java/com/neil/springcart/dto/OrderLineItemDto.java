package com.neil.springcart.dto;

import com.neil.springcart.model.ProductSize;
import jakarta.validation.constraints.NotNull;

public record OrderLineItemDto(
        @NotNull(message = "Missing product ID")
        Long productId,
        @NotNull(message = "Missing product size")
        ProductSize size,
        @NotNull(message = "Missing quantity")
        int quantity
) {}
