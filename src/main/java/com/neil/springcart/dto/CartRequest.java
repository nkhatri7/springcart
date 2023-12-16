package com.neil.springcart.dto;

import jakarta.validation.constraints.NotNull;

public record CartRequest(
   @NotNull(message = "Missing customer ID")
   Long customerId,
   @NotNull(message = "Missing product ID")
   Long productId
) {}
