package com.neil.springcart.service;

import com.neil.springcart.dto.AddInventoryRequest;
import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.InventoryItemRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.mapper.InventoryMapper;
import com.neil.springcart.util.mapper.NewProductMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class InternalProductServiceTest {
    private InternalProductService internalProductService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @BeforeEach
    void setUp() {
        NewProductMapper newProductMapper = new NewProductMapper();
        InventoryMapper inventoryMapper = new InventoryMapper();
        internalProductService = new InternalProductService(productRepository,
                inventoryItemRepository, newProductMapper, inventoryMapper);
    }

    @AfterEach
    void tearDown() {
        reset(productRepository, inventoryItemRepository);
    }

    @Test
    void createProductSavesProductOnce() {
        NewProductRequest request = buildNewProductRequest(new ArrayList<>());
        internalProductService.createProduct(request);
        verify(productRepository, times(1)).save(any());
    }

    @Test
    void addProductInventorySavesTenInventoryItemsWhenThereAreTenItemsInTheRequest() {
        // Given the add inventory request has 10 stock items
        AddInventoryRequest request = new AddInventoryRequest(List.of(
                new InventoryDto(ProductSize.S, 10)
        ));
        Long productId = 1L;
        Product product = buildProduct("name", "description");
        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));
        // When addProductInventory() is called
        internalProductService.addProductInventory(productId, request);
        // Then 10 inventory items are saved
        ArgumentCaptor<List<InventoryItem>> argumentCaptor = ArgumentCaptor
                .forClass(List.class);
        verify(inventoryItemRepository).saveAll(argumentCaptor.capture());
        List<InventoryItem> inventory = argumentCaptor.getValue();
        assertThat(inventory.size()).isEqualTo(10);
    }

    @Test
    void updateProductUpdatesNameIfNameIsInTheRequestAndIsDifferentToTheCurrentValue() {
        // Given a request contains a name property that is different to the
        // current product's name
        String oldName = "old name";
        String newName = "new name";
        Product product = buildProduct(oldName, "description");
        UpdateProductRequest request = buildUpdateProductRequestWithName(
                newName);
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        // When updateProduct() is called
        internalProductService.updateProduct(product.getId(), request);
        // Then the product name is changed
        assertThat(product.getName()).isEqualTo(newName);
    }

    @Test
    void updateProductUpdatesDescriptionIfDescriptionIsInTheRequestAndIsDifferentToTheCurrentValue() {
        // Given a request contains a description property that is different to
        // the current product's description
        String oldDescription = "old description";
        String newDescription = "new description";
        Product product = buildProduct("name", oldDescription);
        UpdateProductRequest request = buildUpdateProductRequestWithDescription(
                newDescription);
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        // When updateProduct() is called
        internalProductService.updateProduct(product.getId(), request);
        // Then the product description is changed
        assertThat(product.getDescription()).isEqualTo(newDescription);
    }

    @Test
    void updateProductUpdatesNothingIfNothingIsInTheRequest() {
        // Given nothing is in the request
        String name = "name";
        String description = "description";
        Product product = buildProduct(name, description);
        UpdateProductRequest request = UpdateProductRequest.builder().build();
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        // When updateProduct() is called
        internalProductService.updateProduct(product.getId(), request);
        // Then nothing is changed
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
    }

    @Test
    void archiveProductArchivesTheProduct() {
        // Given a product is not archived
        Product product = buildProduct("name", "description");
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        // When archiveProduct() is called
        internalProductService.archiveProduct(product.getId());
        // Then the product will be archived
        assertThat(product.isActive()).isFalse();
    }

    @Test
    void unarchiveProductUnarchivesTheProduct() {
        // Given a product is archived
        Product product = buildProduct("name", "description");
        product.setActive(false);
        given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
        // When archiveProduct() is called
        internalProductService.unarchiveProduct(product.getId());
        // Then the product will be unarchived
        assertThat(product.isActive()).isTrue();
    }

    private Product buildProduct(String name, String description) {
        return Product.builder()
                .id(1L)
                .sku(UUID.randomUUID())
                .brand("brand")
                .name(name)
                .description(description)
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .price(50)
                .isActive(true)
                .inventory(new ArrayList<>())
                .build();
    }

    private UpdateProductRequest buildUpdateProductRequestWithName(String name) {
        return UpdateProductRequest.builder()
                .name(name)
                .build();
    }

    private UpdateProductRequest buildUpdateProductRequestWithDescription(String description) {
        return UpdateProductRequest.builder()
                .description(description)
                .build();
    }

    private NewProductRequest buildNewProductRequest(
            List<InventoryDto> inventoryList) {
        return NewProductRequest.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .price(50)
                .inventory(inventoryList)
                .build();
    }
}