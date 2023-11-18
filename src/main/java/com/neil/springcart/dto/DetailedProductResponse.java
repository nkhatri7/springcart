package com.neil.springcart.dto;

import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record DetailedProductResponse(Long id, UUID sku, String brand,
                                      String name, String description,
                                      ProductCategory category,
                                      ProductGender gender,
                                      List<InventoryDto> inventory) {}
