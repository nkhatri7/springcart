package com.neil.springcart.dto;

import lombok.Builder;

@Builder
public record CustomerResponse(Long id, String name, String email) {}
