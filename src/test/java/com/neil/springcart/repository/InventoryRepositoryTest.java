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
class InventoryRepositoryTest {
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
        List<Inventory> productInventory = inventoryRepository
                .findInventoryByProduct(1L);
        assertThat(productInventory.isEmpty()).isTrue();
    }

    @Test
    void findInventoryByProduct_itShouldReturnTwoItemsIfProductHasTwoItems() {
        // Given there are 2 product inventory items
        Product product = buildProduct();
        List<Inventory> inventoryList = List.of(
                buildInventory(ProductSize.S, 10, product),
                buildInventory(ProductSize.M, 20, product)
        );
        product.setInventoryList(inventoryList);
        Product newProduct = productRepository.save(product);
        // When findInventoryByProduct is called
        List<Inventory> productInventory = inventoryRepository
                .findInventoryByProduct(newProduct.getId());
        // A list of 2 items is returned
        assertThat(productInventory.size()).isEqualTo(inventoryList.size());
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

    private Inventory buildInventory(ProductSize size, int stock,
                                     Product product) {
        return Inventory.builder()
                .size(size)
                .stock(stock)
                .product(product)
                .build();
    }
}