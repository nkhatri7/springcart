package com.neil.springcart.repository;

import com.neil.springcart.model.Product;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void findActiveProductsShouldReturnAnEmptyListIfThereAreNoProducts() {
        // Given there are no products in the database
        // When findActiveProducts() is called
        List<Product> products = productRepository.findActiveProducts();
        // Then an empty list is returned
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void findActiveProductsShouldReturnOneProductIfThereIsOneActiveProduct() {
        // Given there are two products but only one is active
        saveProduct("product 1", false);
        saveProduct("product 2", true);
        // When findActiveProducts() is called
        List<Product> products = productRepository.findActiveProducts();
        // Then a list containing only one product is returned
        assertThat(products.size()).isEqualTo(1);
    }

    private void saveProduct(String name, boolean isActive) {
        Product product = buildProduct(name, isActive);
        productRepository.save(product);
    }

    private Product buildProduct(String name, boolean isActive) {
        return Product.builder()
                .brand("brand")
                .name(name)
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.UNISEX)
                .isActive(isActive)
                .inventoryList(new ArrayList<>())
                .build();
    }
}