package com.neil.springcart.dto;

import lombok.Builder;

@Builder
public record UpdateProductRequest(String name, String description) {}
