package com.neil.springcart.model;

import com.neil.springcart.exception.BadRequestException;
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cart_product",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    /**
     * Adds the given product to the list of existing products in the cart.
     * @param product The product to be added to the cart.
     * @throws BadRequestException If the product is already in the cart.
     */
    public void addProduct(Product product) {
        if (isProductInCart(product)) {
            throw new BadRequestException("Product is already in cart");
        }
        this.products.add(product);
    }

    /**
     * Removes the given product from the list of existing products in the cart.
     * @param product The product to be removed from the cart.
     * @throws BadRequestException If the product is not in the cart.
     */
    public void removeProduct(Product product) {
        if (!isProductInCart(product)) {
            throw new BadRequestException("Product is not in cart");
        }
        this.products.remove(product);
    }

    private boolean isProductInCart(Product product) {
        return this.products.contains(product);
    }
}
