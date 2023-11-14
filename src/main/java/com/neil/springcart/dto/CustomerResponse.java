package com.neil.springcart.dto;

import lombok.Builder;

@Builder
public record CustomerResponse(String name, String email) {}
