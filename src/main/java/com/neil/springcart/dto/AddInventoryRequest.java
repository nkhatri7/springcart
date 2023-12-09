package com.neil.springcart.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddInventoryRequest(
        @NotNull List<InventoryDto> inventory
) {}
