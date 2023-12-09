package com.neil.springcart.repository;

import com.neil.springcart.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    @Query("SELECT i FROM InventoryItem i WHERE i.product.id = ?1")
    List<InventoryItem> findInventoryByProduct(Long productId);
}
