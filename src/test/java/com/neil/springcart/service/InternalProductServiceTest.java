package com.neil.springcart.service;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.InventoryRepository;
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
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setUp() {
        InventoryMapper inventoryMapper = new InventoryMapper();
        NewProductMapper newProductMapper = new NewProductMapper(
                inventoryMapper);
        internalProductService = new InternalProductService(productRepository,
                inventoryRepository, newProductMapper, inventoryMapper);
    }

    @AfterEach
    void tearDown() {
        reset(productRepository, inventoryRepository);
    }

    @Test
    void createProductSavesProductOnce() {
        NewProductRequest request = buildNewProductRequest(new ArrayList<>());
        internalProductService.createProduct(request);
        verify(productRepository, times(1)).save(any());
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
        // When updateProduct() is called
        internalProductService.updateProduct(product, request);
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
        // When updateProduct() is called
        internalProductService.updateProduct(product, request);
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
        // When updateProduct() is called
        internalProductService.updateProduct(product, request);
        // Then nothing is changed
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
    }

    @Test
    void updateProductInventoryCreatesNewInventoryItemIfInventoryListHasNewInventorySize() {
        // Given the new updated inventory list has a new size
        Product product = buildProduct("name", "description");
        List<Inventory> productInventory = List.of(
            buildInventory(ProductSize.S, 10, product)
        );
        given(inventoryRepository.findInventoryByProduct(product.getId()))
                .willReturn(productInventory);
        List<InventoryDto> inventoryDtoList = List.of(
            new InventoryDto(ProductSize.S, 20),
            new InventoryDto(ProductSize.M, 20)
        );
        // When updateProductInventory() is called
        internalProductService.updateProductInventory(product,
                inventoryDtoList);
        // Then the new size is added to the database
        ArgumentCaptor<Inventory> argumentCaptor = ArgumentCaptor.forClass(
                Inventory.class);
        verify(inventoryRepository).save(argumentCaptor.capture());
        Inventory capturedInventory = argumentCaptor.getValue();
        assertThat(capturedInventory.getSize()).isEqualTo(ProductSize.M);
    }

    @Test
    void toggleProductActiveStateArchivesTheProductIfItIsNotArchived() {
        // Given a product is not archived
        Product product = buildProduct("name", "description");
        // When toggleProductActiveState() is called
        internalProductService.toggleProductActiveState(product);
        // Then product will be archived
        assertThat(product.isActive()).isFalse();
    }

    @Test
    void toggleProductActiveStateUnarchivesTheProductIfItIsArchived() {
        // Given a product is archived
        Product product = buildProduct("name", "description");
        product.setActive(false);
        // When toggleProductActiveState() is called
        internalProductService.toggleProductActiveState(product);
        // Then product will be unarchived
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
                .isActive(true)
                .inventoryList(new ArrayList<>())
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
                .inventory(inventoryList)
                .build();
    }

    private Admin createAdmin(String email) {
        return Admin.builder()
                .email(email)
                .password("password")
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