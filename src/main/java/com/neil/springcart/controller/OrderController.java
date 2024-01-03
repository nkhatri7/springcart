package com.neil.springcart.controller;

import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.dto.OrderSummary;
import com.neil.springcart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * A controller to handle incoming requests for order related operations.
 */
@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    /**
     * Creates an order with the data from the request.
     * @param request An object containing the customer ID, items to be ordered,
     *                and the shipping address.
     * @return An order summary.
     */
    @Operation(summary = "Creates an order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderSummary createOrder(
            @RequestBody @Valid CreateOrderRequest request) {
        log.info("POST /api/v1/orders");
        return orderService.createOrder(request);
    }
}
