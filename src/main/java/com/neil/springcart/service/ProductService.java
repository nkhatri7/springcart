package com.neil.springcart.service;

import com.neil.springcart.dto.ProductResponse;
import com.neil.springcart.model.Product;
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
        return products.stream()
                .map(productMapper::mapToResponse)
                .toList();
    }
}
