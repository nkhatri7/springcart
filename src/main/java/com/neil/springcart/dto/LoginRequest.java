package com.neil.springcart.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Missing customer email")
        String email,
        @NotBlank(message = "Missing customer password")
        String password
) {}
