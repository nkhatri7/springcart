package com.neil.springcart.controller;

import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Creates an order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createOrder(@RequestBody @Valid CreateOrderRequest request) {
        log.info("POST /api/v1/orders");
        return orderService.createOrder(request);
    }
}
