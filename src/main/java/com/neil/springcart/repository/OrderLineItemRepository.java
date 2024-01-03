package com.neil.springcart.repository;

import com.neil.springcart.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderLineItemRepository
        extends JpaRepository<OrderLineItem, Long> {
    /**
     * Finds order line items linked to the order with the given ID.
     * @param orderId The ID of the order.
     * @return A list of order line items linked to the order with the given ID.
     */
    @Query("SELECT i FROM OrderLineItem i WHERE i.order.id = ?1")
    List<OrderLineItem> findOrderLineItemsByOrderId(Long orderId);
}
