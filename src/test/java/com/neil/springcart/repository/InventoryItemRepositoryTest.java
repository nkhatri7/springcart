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
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        inventoryItemRepository.deleteAll();
    }

    @Test
    void findInventoryByProduct_itShouldReturnEmptyListIfProductHasNoInventoryItems() {
        List<InventoryItem> productInventoryItem = inventoryItemRepository
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
        List<InventoryItem> productInventoryItem = inventoryItemRepository
                .findInventoryByProduct(newProduct.getId());
        // A list of 2 items is returned
        assertThat(productInventoryItem.size()).isEqualTo(inventory.size());
    }

    @Test
    void findInventoryByProductAndSizeShouldReturnAnEmptyListIfTheProductHasNoItemsInSmallSizeAndSmallSizeIsBeingSearched() {
        // Given there is one size medium inventory item for a product
        Product product = buildProduct();
        List<InventoryItem> inventory = List.of(
                buildInventory(ProductSize.M, product)
        );
        product.setInventory(inventory);
        productRepository.save(product);
        // When findInventoryByProductAndSize() is called with a search for
        // size small
        List<InventoryItem> productInventory = inventoryItemRepository
                .findInventoryByProductAndSize(product.getId(), ProductSize.S);
        // Then an empty list is returned
        assertThat(productInventory.size()).isEqualTo(0);
    }

    @Test
    void findInventoryByProductAndSizeShouldReturnOneItemIfTheProductHasOneSmallItemAndSmallSizeIsBeingSearched() {
        // Given there is one size small inventory item for a product
        Product product = buildProduct();
        List<InventoryItem> inventory = List.of(
                buildInventory(ProductSize.S, product)
        );
        product.setInventory(inventory);
        productRepository.save(product);
        // When findInventoryByProductAndSize() is called with a search for
        // size small
        List<InventoryItem> productInventory = inventoryItemRepository
                .findInventoryByProductAndSize(product.getId(), ProductSize.S);
        // Then one inventory item is returned
        assertThat(productInventory.size()).isEqualTo(1);
    }

    @Test
    void findInventoryByProductAndSizeShouldReturnAnEmptyListIfTheProductHasOneSmallItemThatIsSoldAndSmallSizeIsBeingSearched() {
        // Given there is one small size inventory item for a product, but it
        // has already been sold
        Product product = buildProduct();
        InventoryItem inventoryItem = buildInventory(ProductSize.S, product);
        inventoryItem.setSold(true);
        product.setInventory(List.of(inventoryItem));
        productRepository.save(product);
        // When findInventoryByProductAndSize() is called with a search for
        // size small
        List<InventoryItem> productInventory = inventoryItemRepository
                .findInventoryByProductAndSize(product.getId(), ProductSize.S);
        // Then an empty list is returned
        assertThat(productInventory.size()).isEqualTo(0);
    }

    private Product buildProduct() {
        return Product.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .price(50)
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