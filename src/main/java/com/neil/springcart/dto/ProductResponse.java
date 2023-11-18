package com.neil.springcart.dto;

import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import lombok.Builder;

@Builder
public record ProductResponse(Long id, String brand, String name,
                              ProductCategory category, ProductGender gender) {}
