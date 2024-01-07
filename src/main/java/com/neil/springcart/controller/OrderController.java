package com.neil.springcart.controller;

import com.neil.springcart.dto.CreateOrderRequest;
import com.neil.springcart.dto.OrderResponse;
import com.neil.springcart.dto.OrderSummary;
import com.neil.springcart.service.OrderService;
import com.neil.springcart.util.HttpUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        log.info("POST {}", HttpUtil.getCurrentRequestPath());
        return orderService.createOrder(request);
    }

    /**
     * Gets data on all of a customer's orders.
     * @param customerId The ID of a customer.
     * @return A list of order summaries for a customer.
     */
    @Operation(summary = "Gets a customer's orders")
    @GetMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderSummary> getCustomerOrders(@PathVariable Long customerId) {
        log.info("GET {}", HttpUtil.getCurrentRequestPath());
        return orderService.getCustomerOrders(customerId);
    }

    /**
     * Gets all the order details for the order with the given ID.
     * @param id The ID of the order.
     * @return The order details.
     */
    @Operation(summary = "Gets an order's details")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderDetails(@PathVariable Long id) {
        log.info("GET {}", HttpUtil.getCurrentRequestPath());
        return orderService.getOrderDetails(id);
    }
}
