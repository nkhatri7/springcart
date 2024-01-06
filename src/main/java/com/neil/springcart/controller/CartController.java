package com.neil.springcart.controller;

import com.neil.springcart.dto.CartRequest;
import com.neil.springcart.dto.CartResponse;
import com.neil.springcart.service.CartService;
import com.neil.springcart.util.HttpUtil;
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
     * Gets a customer's cart details.
     * @param customerId The customer ID.
     * @return The cart details.
     */
    @Operation(summary = "Gets a customer's cart details")
    @GetMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCustomerCart(@PathVariable Long customerId) {
        log.info("GET {}", HttpUtil.getCurrentRequestPath());
        return cartService.getCustomerCartDetails(customerId);
    }

    /**
     * Adds a product to a customer's cart.
     * @param request A request containing the customer ID and product ID.
     */
    @Operation(summary = "Adds a product to a customer's cart")
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void addProductToCart(@RequestBody @Valid CartRequest request) {
        log.info("POST {}", HttpUtil.getCurrentRequestPath());
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
        log.info("POST {}", HttpUtil.getCurrentRequestPath());
        cartService.removeProductFromCart(request);
    }
}
