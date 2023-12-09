package com.neil.springcart.dto;

import com.neil.springcart.model.ProductSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record InventoryDto(
    @NotBlank(message = "Invalid product size")
    ProductSize size,
    @NotNull(message = "Missing stock")
    int stock
) {}
