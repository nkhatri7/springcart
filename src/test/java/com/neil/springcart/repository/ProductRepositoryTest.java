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

    @Test
    void findProductsByGenderShouldReturnAnEmptyListIfThereAreNoProductsForTheGivenGender() {
        // Given there is one female product
        saveProduct("female product", ProductGender.FEMALE);
        // When findProductsByGender() is called with MALE
        List<Product> products = productRepository
                .findProductsByGender(ProductGender.MALE);
        // Then an empty list will be returned
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void findProductsByGenderShouldReturnOneProductIfThereIsOneProductForTheGivenGender() {
        // Given there is one female product
        ProductGender gender = ProductGender.FEMALE;
        saveProduct("female product", gender);
        // When findProductsByGender() is called with FEMALE
        List<Product> products = productRepository.findProductsByGender(gender);
        // Then one product will be returned
        assertThat(products.size()).isEqualTo(1);
    }

    @Test
    void findProductsByGenderShouldReturnTwoProductsIfThereIsOneProductForTheGivenGenderAndOneUnisexProduct() {
        // Given there is one male product and one unisex product
        ProductGender gender = ProductGender.MALE;
        saveProduct("male product", gender);
        saveProduct("unisex product", ProductGender.UNISEX);
        // When findProductsById() is called with MALE
        List<Product> products = productRepository.findProductsByGender(gender);
        // Then two products will be returned
        assertThat(products.size()).isEqualTo(2);
    }

    private void saveProduct(String name, ProductGender gender) {
        Product product = buildProduct(name, gender, true);
        productRepository.save(product);
    }

    private void saveProduct(String name, boolean isActive) {
        Product product = buildProduct(name, ProductGender.UNISEX, isActive);
        productRepository.save(product);
    }

    private Product buildProduct(String name, ProductGender gender,
                                 boolean isActive) {
        return Product.builder()
                .brand("brand")
                .name(name)
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(gender)
                .isActive(isActive)
                .inventoryList(new ArrayList<>())
                .build();
    }
}