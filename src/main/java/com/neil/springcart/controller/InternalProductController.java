package com.neil.springcart.controller;

import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductInventoryRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.exception.ForbiddenException;
import com.neil.springcart.exception.NotFoundException;
import com.neil.springcart.model.Product;
import com.neil.springcart.service.InternalProductService;
import com.neil.springcart.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * A controller to handle incoming requests for internal product requests (i.e.
 * product and inventory management).
 */
@RestController
@RequestMapping("/internal/products")
@AllArgsConstructor
@Slf4j
public class InternalProductController {
    private final InternalProductService internalProductService;
    private final AuthUtil authUtil;

    /**
     * Handles incoming requests to create a product in the database.
     * @param request The request body.
     * @throws ForbiddenException If the user making the request is not an
     * admin.
     */
    @Operation(summary = "Creates a new product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void handleNewProduct(
            @RequestBody @Valid NewProductRequest request) {
        log.info("POST /internal/products");

        if (!authUtil.isAdmin()) {
            throw new ForbiddenException("User is not an admin");
        }

        Product product = internalProductService.createProduct(request);
        log.info("Product created (ID: {})", product.getId());
    }

    /**
     * Handles incoming requests to update product details.
     * @param id The ID of the product.
     * @param request The request body.
     * @throws ForbiddenException If the user making the request is not an
     * admin.
     * @throws NotFoundException If a product with that ID does not exist.
     * @throws BadRequestException If the product with that ID is inactive.
     */
    @Operation(summary = "Updates a product's details")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void handleProductUpdate(@PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        log.info("PATCH /internal/products/{}", id);

        if (!authUtil.isAdmin()) {
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
        log.info("Product updated (ID: {})", id);
    }

    /**
     * Handles incoming requests to update product inventory.
     * @param id The ID of the product.
     * @param request The request body.
     * @throws ForbiddenException If the user making the request is not an
     * admin.
     * @throws NotFoundException If a product with that ID does not exist.
     * @throws BadRequestException If the product with that ID is inactive.
     */
    @Operation(summary = "Updates a product's inventory")
    @PatchMapping("/{id}/inventory")
    @ResponseStatus(HttpStatus.OK)
    public void handleProductInventoryUpdate(@PathVariable Long id,
            @RequestBody UpdateProductInventoryRequest request) {
        log.info("PATCH /internal/products/{}/inventory", id);

        if (!authUtil.isAdmin()) {
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
        log.info("Product inventory updated (ID: {})", id);
    }

    /**
     * Handles incoming requests to archive a product.
     * @param id The ID of the product.
     * @throws ForbiddenException If the user making the request is not an
     * admin.
     * @throws NotFoundException If a product with that ID does not exist.
     * @throws BadRequestException If the product with that ID is already
     * archived.
     */
    @Operation(summary = "Archives a product")
    @PatchMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.OK)
    public void handleArchiveProduct(@PathVariable Long id) {
        log.info("PATCH /internal/products/{}/archive", id);

        if (!authUtil.isAdmin()) {
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
        log.info("Product archived (ID: {})", product.getId());
    }

    /**
     * Handles incoming requests to unarchive a product.
     * @param id The ID of the product.
     * @throws ForbiddenException If the user making the request is not an
     * admin.
     * @throws NotFoundException If a product with that ID does not exist.
     * @throws BadRequestException If the product with that ID is not archived.
     */
    @Operation(summary = "Unarchives a product")
    @PatchMapping("/{id}/unarchive")
    @ResponseStatus(HttpStatus.OK)
    public void handleUnarchiveProduct(@PathVariable Long id) {
        log.info("PATCH /internal/products/{}/unarchive", id);

        if (!authUtil.isAdmin()) {
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
        log.info("Product unarchived (ID: {})", product.getId());
    }
}
