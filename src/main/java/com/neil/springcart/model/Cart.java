package com.neil.springcart.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "Cart")
@Table(name = "cart")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @SequenceGenerator(
            name = "cart_sequence",
            sequenceName = "cart_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_sequence"
    )
    private Long id;
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToMany
    @JoinTable(
            name = "cart_product",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    /**
     * Adds the given product to the list of existing products in the cart.
     * @param product The product to be added to the cart.
     */
    public void addProduct(Product product) {
        this.products.add(product);
    }

    /**
     * Checks if the given product is already in the cart.
     * @param product The product being checked.
     * @return {@code true} if the product is in the cart, {@code false} if not.
     */
    public boolean isProductInCart(Product product) {
        return this.products.contains(product);
    }
}
