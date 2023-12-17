package com.neil.springcart.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(Long cartId, List<ProductResponse> items) {}
