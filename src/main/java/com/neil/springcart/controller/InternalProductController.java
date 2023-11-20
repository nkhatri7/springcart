package com.neil.springcart.controller;

import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductInventoryRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.exception.ForbiddenException;
import com.neil.springcart.exception.NotFoundException;
import com.neil.springcart.model.Product;
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

        Product product = internalProductService.createProduct(request);
        log.info("Product (ID: {}) created", product.getId());
    }

    /**
     * Handles incoming requests to update product details.
     * @param authHeader The Authorization header from the request.
     * @param id The ID of the product.
     * @param request The request body.
     * @throws ForbiddenException If the user making the request is not an
     * admin.
     * @throws NotFoundException If a product with that ID does not exist.
     * @throws BadRequestException If the product with that ID is inactive.
     */
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void handleProductUpdate(@PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody UpdateProductRequest request) {
        log.info("PATCH /internal/products/{}", id);

        if (!internalProductService.isAdmin(authHeader)) {
            throw new ForbiddenException("User is not an admin");
        }
        // Check if product with ID exists
        Product product = internalProductService.getProduct(id)
                .orElseThrow(() -> new NotFoundException(
                        "Product with ID " + id + " does not exist"));
        // Check if product is active
        if (!product.isActive()) {
            throw new BadRequestException("Product is not active");
        }

        internalProductService.updateProduct(product, request);
        log.info("Product (ID: {}) updated", id);
    }

    /**
     * Handles incoming requests to update product inventory.
     * @param id THe ID of the product.
     * @param authHeader The Authorization header from the request.
     * @param request The request body.
     * @throws ForbiddenException If the user making the request is not an
     * admin.
     * @throws NotFoundException If a product with that ID does not exist.
     * @throws BadRequestException If the product with that ID is inactive.
     */
    @PatchMapping("/{id}/inventory")
    public void handleProductInventoryUpdate(@PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody UpdateProductInventoryRequest request) {
        log.info("PATCH /internal/products/{}/inventory", id);

        if (!internalProductService.isAdmin(authHeader)) {
            throw new ForbiddenException("User is not an admin");
        }
        // Check if product with ID exists
        Product product = internalProductService.getProduct(id)
                .orElseThrow(() -> new NotFoundException(
                        "Product with ID " + id + " does not exist"));
        // Check if product is active
        if (!product.isActive()) {
            throw new BadRequestException("Product is not active");
        }

        internalProductService.updateProductInventory(product,
                request.inventory());
        log.info("Inventory for product (ID: {}) updated", id);
    }

    @PatchMapping("/{id}/archive")
    public void handleArchiveProduct(@PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        log.info("PATCH /internal/products/{}/archive", id);

        if (!internalProductService.isAdmin(authHeader)) {
            throw new ForbiddenException("User is not an admin");
        }
        // Check if product with ID exists
        Product product = internalProductService.getProduct(id)
                .orElseThrow(() -> new NotFoundException(
                        "Product with ID " + id + " does not exist"));
        // Check if product is already archived
        if (!product.isActive()) {
            throw new BadRequestException("Product is already archived");
        }

        internalProductService.toggleProductActiveState(product);
        log.info("Product (ID: {}) has been archived", product.getId());
    }

    @PatchMapping("/{id}/unarchive")
    public void handleUnarchiveProduct(@PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        log.info("PATCH /internal/products/{}/unarchive", id);

        if (!internalProductService.isAdmin(authHeader)) {
            throw new ForbiddenException("User is not an admin");
        }
        // Check if product with ID exists
        Product product = internalProductService.getProduct(id)
                .orElseThrow(() -> new NotFoundException(
                        "Product with ID " + id + " does not exist"));
        // Check if product is already archived
        if (product.isActive()) {
            throw new BadRequestException("Product is not archived");
        }

        internalProductService.toggleProductActiveState(product);
        log.info("Product (ID: {}) has been unarchived", product.getId());
    }
}
