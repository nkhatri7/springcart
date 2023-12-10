package com.neil.springcart.dto;

import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record NewProductRequest(
   @NotBlank(message = "Brand cannot be empty")
   String brand,
   @NotBlank(message = "Name cannot be empty")
   String name,
   @NotBlank(message = "Description cannot be empty")
   String description,
   @NotNull(message = "Invalid product category")
   ProductCategory category,
   @NotNull(message = "Invalid product gender")
   ProductGender gender,
   @NotNull(message = "Missing price")
   double price,
   @NotNull
   List<InventoryDto> inventory
) {}
