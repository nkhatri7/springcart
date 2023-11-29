package com.neil.springcart.repository;

import com.neil.springcart.model.Product;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    List<Product> findActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.gender = ?1 OR p.gender = UNISEX")
    List<Product> findProductsByGender(ProductGender gender);

    @Query("SELECT p FROM Product p WHERE (p.gender = ?1 OR p.gender = UNISEX) AND p.category = ?2")
    List<Product> findProductsByGenderAndCategory(ProductGender gender,
                                                  ProductCategory category);
}
