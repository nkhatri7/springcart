package com.neil.springcart.repository;

import com.neil.springcart.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InventoryItemRepositoryTest {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        inventoryRepository.deleteAll();
    }

    @Test
    void findInventoryByProduct_itShouldReturnEmptyListIfProductHasNoInventoryItems() {
        List<InventoryItem> productInventoryItem = inventoryRepository
                .findInventoryByProduct(1L);
        assertThat(productInventoryItem.isEmpty()).isTrue();
    }

    @Test
    void findInventoryByProduct_itShouldReturnTwoItemsIfProductHasTwoItems() {
        // Given there are 2 product inventory items
        Product product = buildProduct();
        List<InventoryItem> inventory = List.of(
                buildInventory(ProductSize.S, product),
                buildInventory(ProductSize.M, product)
        );
        product.setInventory(inventory);
        Product newProduct = productRepository.save(product);
        // When findInventoryByProduct is called
        List<InventoryItem> productInventoryItem = inventoryRepository
                .findInventoryByProduct(newProduct.getId());
        // A list of 2 items is returned
        assertThat(productInventoryItem.size()).isEqualTo(inventory.size());
    }

    private Product buildProduct() {
        return Product.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .isActive(true)
                .build();
    }

    private InventoryItem buildInventory(ProductSize size, Product product) {
        return InventoryItem.builder()
                .size(size)
                .product(product)
                .build();
    }
}