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
    void findAllByGenderShouldReturnAnEmptyListIfThereAreNoProductsForTheGivenGender() {
        // Given there is one female product
        saveProduct("female product", ProductGender.FEMALE);
        // When findAllByGender() is called with MALE
        List<Product> products = productRepository
                .findAllByGender(ProductGender.MALE);
        // Then an empty list will be returned
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void findAllByGenderShouldReturnOneProductIfThereIsOneProductForTheGivenGender() {
        // Given there is one female product
        ProductGender gender = ProductGender.FEMALE;
        saveProduct("female product", gender);
        // When findAllByGender() is called with FEMALE
        List<Product> products = productRepository.findAllByGender(gender);
        // Then one product will be returned
        assertThat(products.size()).isEqualTo(1);
    }

    @Test
    void findAllByGenderShouldReturnTwoProductsIfThereIsOneProductForTheGivenGenderAndOneUnisexProduct() {
        // Given there is one male product and one unisex product
        ProductGender gender = ProductGender.MALE;
        saveProduct("male product", gender);
        saveProduct("unisex product", ProductGender.UNISEX);
        // When findAllByGender() is called with MALE
        List<Product> products = productRepository.findAllByGender(gender);
        // Then two products will be returned
        assertThat(products.size()).isEqualTo(2);
    }

    @Test
    void findAllByGenderAndCategoryShouldReturnOneProductIfThereIsOneProductWithTheGivenGenderAndCategory() {
        // Given there are two products with one being MALE and SPORTSWEAR
        // and the other being FEMALE and SPORTSWEAR
        ProductGender gender = ProductGender.MALE;
        ProductCategory category = ProductCategory.SPORTSWEAR;
        saveProduct("male sportswear product", gender, category);
        saveProduct("female sportswear product", ProductGender.FEMALE,
                category);
        // When findAllByGenderAndCategory() is called with MALE and
        // SPORTSWEAR
        List<Product> products = productRepository
                .findAllByGenderAndCategory(gender, category);
        // Then one product will be returned
        assertThat(products.size()).isEqualTo(1);
    }

    private void saveProduct(String name, ProductGender gender,
                             ProductCategory category) {
        Product product = buildProduct(name, gender, category, true);
        productRepository.save(product);
    }

    private void saveProduct(String name, ProductGender gender) {
        Product product = buildProduct(name, gender, ProductCategory.SPORTSWEAR,
                true);
        productRepository.save(product);
    }

    private void saveProduct(String name, boolean isActive) {
        Product product = buildProduct(name, ProductGender.UNISEX,
                ProductCategory.SPORTSWEAR, isActive);
        productRepository.save(product);
    }

    private Product buildProduct(String name, ProductGender gender,
                                 ProductCategory category, boolean isActive) {
        return Product.builder()
                .brand("brand")
                .name(name)
                .description("description")
                .category(category)
                .gender(gender)
                .isActive(isActive)
                .inventory(new ArrayList<>())
                .build();
    }
}