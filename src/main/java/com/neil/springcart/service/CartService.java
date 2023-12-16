package com.neil.springcart.service;

import com.neil.springcart.dto.CartRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Cart;
import com.neil.springcart.model.Product;
import com.neil.springcart.repository.CartRepository;
import com.neil.springcart.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Adds a product to a customer's cart.
     * @param request A request containing the customer ID and product ID.
     */
    @Transactional
    public void addProductToCart(CartRequest request) {
        Product product = getActiveProduct(request.productId());
        Cart cart = getCustomerCart(request.customerId());
        if (cart.isProductInCart(product)) {
            throw new BadRequestException("Product is already in cart");
        }
        cart.addProduct(product);
        log.info("Product (ID: {}) added to cart (ID: {})", request.productId(),
                request.customerId());
    }

    private Product getActiveProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new BadRequestException("Product with ID " + id + " does not exist")
        );
        if (!product.isActive()) {
            throw new BadRequestException("Product is not active");
        }
        return product;
    }

    private Cart getCustomerCart(Long customerId) {
        // Cart ID will be the same as Customer ID
        return cartRepository.findById(customerId).orElseThrow(() ->
            new BadRequestException("Invalid request")
        );
    }
}
