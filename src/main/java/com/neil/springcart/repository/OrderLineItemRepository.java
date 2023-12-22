package com.neil.springcart.repository;

import com.neil.springcart.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineItemRepository
        extends JpaRepository<OrderLineItem, Long> {
}
