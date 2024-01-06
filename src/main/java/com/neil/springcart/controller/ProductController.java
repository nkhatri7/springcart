package com.neil.springcart.controller;

import com.neil.springcart.dto.DetailedProductResponse;
import com.neil.springcart.dto.ProductResponse;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import com.neil.springcart.service.ProductService;
import com.neil.springcart.util.HttpUtil;
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
        log.info("GET {}", HttpUtil.getCurrentRequestPath());
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
        log.info("GET {}", HttpUtil.getCurrentRequestPath());
        return productService.getProductsByGender(gender);
    }

    /**
     * Handles incoming requests to get all active products for a gender and
     * category.
     * @return A list of all the active products for a gender and category.
     */
    @Operation(
            summary = "Gets all the active products for a gender and category"
    )
    @GetMapping(params = { "gender", "category" })
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductsByGenderAndCategory(
            @RequestParam ProductGender gender,
            @RequestParam ProductCategory category) {
        log.info("GET {}", HttpUtil.getCurrentRequestPath());
        return productService.getProductsByGenderAndCategory(gender, category);
    }

    /**
     * Handles incoming requests to get the product with the given ID.
     * @param id The ID of the product.
     * @return The data for the product with the given ID.
     */
    @Operation(summary = "Gets the product data for the product with the ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DetailedProductResponse getProduct(@PathVariable Long id) {
        log.info("GET {}", HttpUtil.getCurrentRequestPath());
        return productService.getProductById(id);
    }
}
