package com.neil.springcart.service;

import com.neil.springcart.dto.DetailedProductResponse;
import com.neil.springcart.dto.ProductResponse;
import com.neil.springcart.exception.NotFoundException;
import com.neil.springcart.model.Product;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.mapper.ProductMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * Gets all active products.
     * @return A list of all the active products.
     */
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findActiveProducts();
        log.info("{} active products found", products.size());
        return productMapper.mapListToResponse(products);
    }

    /**
     * Gets all active products for the given gender.
     * @param gender The gender of the products.
     * @return A list of products for the given gender.
     */
    public List<ProductResponse> getProductsByGender(ProductGender gender) {
        List<Product> products = productRepository.findProductsByGender(gender);
        log.info("{} active {} products found", products.size(), gender);
        return productMapper.mapListToResponse(products);
    }

    /**
     * Gets all active products for the given gender and category.
     * @param gender The gender of the products.
     * @param category The category of the products.
     * @return A list of products for the given gender and category
     */
    public List<ProductResponse> getProductsByGenderAndCategory(
            ProductGender gender, ProductCategory category) {
        List<Product> products = productRepository
                .findProductsByGenderAndCategory(gender, category);
        log.info("{} active {} {} products found", products.size(), gender,
                category);
        return productMapper.mapListToResponse(products);
    }

    /**
     * Gets the product with the given ID.
     * @param id The ID of the product.
     * @return The product with the given ID if it exists.
     * @throws NotFoundException If a product with the given ID doesn't exist.
     */
    public DetailedProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new NotFoundException("Product with ID" + id + " doesn't exist")
        );
        log.info("Product (ID: {}) retrieved from database", product.getId());
        return productMapper.mapToDetailedResponse(product);
    }
}
