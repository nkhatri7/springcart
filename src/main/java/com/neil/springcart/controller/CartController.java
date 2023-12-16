package com.neil.springcart.controller;

import com.neil.springcart.dto.CartRequest;
import com.neil.springcart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * A controller to handle incoming requests for customer cart operations.
 */
@RestController
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    /**
     * Adds a product to a customer's cart.
     * @param request A request containing the customer ID and product ID.
     */
    @Operation(summary = "Adds a product to a customer's cart")
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void addProductToCart(@RequestBody @Valid CartRequest request) {
        log.info("POST /api/v1/cart/add");
        cartService.addProductToCart(request);
    }

    /**
     * Removes a product from a customer's cart.
     * @param request A request containing the customer ID and product ID.
     */
    @Operation(summary = "Removes a product from a customer's cart")
    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeProductFromCart(@RequestBody @Valid CartRequest request) {
        log.info("POST /api/v1/cart/remove");
        cartService.removeProductFromCart(request);
    }
}
