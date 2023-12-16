package com.neil.springcart.repository;

import com.neil.springcart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.customer.id = ?1")
    Optional<Cart> findByCustomerId(Long customerId);
}
