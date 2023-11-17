package com.neil.springcart.dto;

import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record NewProductRequest(
   @NotBlank
   String brand,
   @NotBlank
   String name,
   @NotBlank
   String description,
   @NotNull(message = "Invalid product category")
   ProductCategory category,
   @NotNull(message = "Invalid product gender")
   ProductGender gender,
   @NotNull
   List<InventoryDto> inventory
) {}
