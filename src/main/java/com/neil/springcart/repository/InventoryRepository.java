package com.neil.springcart.repository;

import com.neil.springcart.model.InventoryItem;
import com.neil.springcart.model.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    @Query("SELECT i FROM InventoryItem i WHERE i.product.id = ?1 AND i.isSold = false")
    List<InventoryItem> findInventoryByProduct(Long productId);

    /**
     * Finds available inventory items for the product with the given ID that
     * match the given size.
     * @param productId The ID of the product the inventory is for.
     * @param size The size of the inventory items being searched for.
     * @return A list of available inventory items that match the given size for
     * the product with the given ID.
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.product.id = ?1 AND i.size = ?2 AND i.isSold = false")
    List<InventoryItem> findInventoryByProductAndSize(Long productId,
                                                      ProductSize size);
}
