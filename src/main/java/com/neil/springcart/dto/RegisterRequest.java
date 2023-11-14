package com.neil.springcart.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Missing customer name")
        String name,
        @NotBlank(message = "Missing customer email")
        String email,
        @NotBlank(message = "Missing customer password")
        String password
) {}
