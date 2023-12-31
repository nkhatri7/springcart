package com.neil.springcart.controller;

import com.neil.springcart.annotations.IsAdmin;
import com.neil.springcart.dto.AddInventoryRequest;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.model.Product;
import com.neil.springcart.service.InternalProductService;
import com.neil.springcart.util.HttpUtil;
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
@IsAdmin
@RequestMapping("/internal/products")
@AllArgsConstructor
@Slf4j
public class InternalProductController {
    private final InternalProductService internalProductService;

    /**
     * Handles incoming requests to create a product in the database.
     * @param request The request body.
     */
    @Operation(summary = "Creates a new product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void handleNewProduct(
            @RequestBody @Valid NewProductRequest request) {
        log.info("POST {}", HttpUtil.getCurrentRequestPath());
        Product product = internalProductService.createProduct(request);
        log.info("Product created (ID: {})", product.getId());
    }

    /**
     * Handles incoming requests to update product details.
     * @param id The ID of the product.
     * @param request The request body.
     */
    @Operation(summary = "Updates a product's details")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void handleProductUpdate(@PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        log.info("PATCH {}", HttpUtil.getCurrentRequestPath());
        internalProductService.updateProduct(id, request);
        log.info("Product updated (ID: {})", id);
    }

    /**
     * Handles incoming requests to add inventory for a product.
     * @param id The ID of the product.
     * @param request The request body.
     */
    @Operation(summary = "Adds inventory for a product")
    @PostMapping("/{id}/inventory")
    @ResponseStatus(HttpStatus.OK)
    public void handleAddInventory(@PathVariable Long id,
            @RequestBody @Valid AddInventoryRequest request) {
        log.info("POST {}", HttpUtil.getCurrentRequestPath());
        internalProductService.addProductInventory(id, request);
        log.info("Inventory added for product (ID: {})", id);
    }

    /**
     * Handles incoming requests to archive a product.
     * @param id The ID of the product.
     */
    @Operation(summary = "Archives a product")
    @PatchMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.OK)
    public void handleArchiveProduct(@PathVariable Long id) {
        log.info("PATCH {}", HttpUtil.getCurrentRequestPath());
        internalProductService.archiveProduct(id);
        log.info("Product archived (ID: {})", id);
    }

    /**
     * Handles incoming requests to unarchive a product.
     * @param id The ID of the product.
     */
    @Operation(summary = "Unarchives a product")
    @PatchMapping("/{id}/unarchive")
    @ResponseStatus(HttpStatus.OK)
    public void handleUnarchiveProduct(@PathVariable Long id) {
        log.info("PATCH {}", HttpUtil.getCurrentRequestPath());
        internalProductService.unarchiveProduct(id);
        log.info("Product unarchived (ID: {})", id);
    }
}
