package com.neil.springcart.controller;

import com.neil.springcart.dto.ProductResponse;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import com.neil.springcart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A controller to handle incoming requests for product requests.
 */
@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    /**
     * Handles incoming requests to get all active products.
     * @return A list of all the active products.
     */
    @Operation(summary = "Gets all the active products")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        log.info("GET /api/v1/products");
        return productService.getAllProducts();
    }

    /**
     * Handles incoming requests to get all active products for a gender.
     * @return A list of all the active products for a gender.
     */
    @Operation(summary = "Gets all the active products for a gender")
    @GetMapping(params = "gender")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProductsByGender(
            @RequestParam ProductGender gender) {
        log.info("GET /api/v1/products?gender={}", gender);
        return productService.getProductsByGender(gender);
    }

    /**
     * Handles incoming requests to get all active products for a gender and
     * category.
     * @return A list of all the active products for a gender and category.
     */
    @Operation(summary = "Gets all the active products for a gender")
    @GetMapping(params = { "gender", "category" })
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductsByGenderAndCategory(
            @RequestParam ProductGender gender,
            @RequestParam ProductCategory category) {
        log.info("GET /api/v1/products?gender={}&category={}", gender,
                category);
        return productService.getProductsByGenderAndCategory(gender, category);
    }
}
