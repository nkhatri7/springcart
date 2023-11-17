package com.neil.springcart.controller;

import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.exception.ForbiddenException;
import com.neil.springcart.service.InternalProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/products")
@AllArgsConstructor
@Slf4j
public class InternalProductController {
    private final InternalProductService internalProductService;

    /**
     * Handles incoming requests to create a product in the database.
     * @param authHeader The Authorization header from the request.
     * @param request The request body.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void handleNewProduct(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody @Valid NewProductRequest request) {
        log.info("POST /internal/products");

        if (!internalProductService.isAdmin(authHeader)) {
            throw new ForbiddenException("User is not an admin");
        }
        internalProductService.createProduct(request);
    }
}
