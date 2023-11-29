package com.neil.springcart.service;

import com.neil.springcart.dto.ProductResponse;
import com.neil.springcart.model.Product;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.mapper.ProductMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductServiceTest {
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        ProductMapper productMapper = new ProductMapper();
        productService = new ProductService(productRepository, productMapper);
    }

    @AfterEach
    void tearDown() {
        reset(productRepository);
    }

    @Test
    void getAllProductsShouldReturnAnEmptyListIfThereAreNoActiveProducts() {
        // Given there are no active products
        given(productRepository.findActiveProducts())
                .willReturn(new ArrayList<>());
        // When getAllProducts() is called
        List<ProductResponse> products = productService.getAllProducts();
        // Then an empty list will be returned
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void getAllProductsShouldReturnOneProductIfThereIsOnlyOneActiveProduct() {
        // Given there is 1 active product
        given(productRepository.findActiveProducts())
                .willReturn(List.of(buildProduct(1L, "product 1")));
        // When getAllProducts() is called
        List<ProductResponse> products = productService.getAllProducts();
        // Then one product will be returned
        assertThat(products.size()).isEqualTo(1);
    }

    @Test
    void getProductsByGenderShouldReturnAnEmptyListIfThereAreNoProductsForTheGivenGender() {
        // Given there are no MALE products
        ProductGender gender = ProductGender.MALE;
        given(productRepository.findProductsByGender(gender))
                .willReturn(new ArrayList<>());
        // When getProductsByGender() is called with MALE
        List<ProductResponse> products = productService
                .getProductsByGender(gender);
        // Then an empty list will be returned
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void getProductsByGenderShouldReturnOneProductIfThereIsOneProductForTheGivenGender() {
        // Given there is one MALE product
        ProductGender gender = ProductGender.MALE;
        Product product = buildProductWithGender(1L, "male product", gender);
        given(productRepository.findProductsByGender(gender))
                .willReturn(List.of(product));
        // When getProductsByGender() is called with MALE
        List<ProductResponse> products = productService
                .getProductsByGender(gender);
        // Then one product is returned
        assertThat(products.size()).isEqualTo(1);
    }

    @Test
    void getProductsByGenderAndCategoryShouldReturnOneProductIfThereIsOneProductWithTheGivenGenderAndCategory() {
        // Given there is one MALE SPORTSWEAR product
        ProductGender gender = ProductGender.MALE;
        ProductCategory category = ProductCategory.SPORTSWEAR;
        Product product = buildProductWithGenderAndCategory(1L,
                "male sportswear product", gender, category);
        given(productRepository.findProductsByGenderAndCategory(
                gender, category)).willReturn(List.of(product));
        // When getProductsByGenderAndCategory() is called with MALE and
        // SPORTSWEAR
        List<ProductResponse> products = productService
                .getProductsByGenderAndCategory(gender, category);
        // Then one product is returned
        assertThat(products.size()).isEqualTo(1);
    }

    private Product buildProductWithGenderAndCategory(Long id, String name,
                                                      ProductGender gender,
                                                      ProductCategory category) {
        Product product = buildProductWithGender(id, name, gender);
        product.setCategory(category);
        return product;
    }

    private Product buildProductWithGender(Long id, String name,
                                           ProductGender gender) {
        Product product = buildProduct(id, name);
        product.setGender(gender);
        return product;
    }

    private Product buildProduct(Long id, String name) {
        return Product.builder()
                .id(id)
                .sku(UUID.randomUUID())
                .brand("brand")
                .name(name)
                .description("description")
                .gender(ProductGender.UNISEX)
                .category(ProductCategory.SPORTSWEAR)
                .isActive(true)
                .inventoryList(new ArrayList<>())
                .build();
    }
}