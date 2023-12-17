package com.neil.springcart.service;

import com.neil.springcart.dto.CartRequest;
import com.neil.springcart.dto.CartResponse;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.Cart;
import com.neil.springcart.model.Product;
import com.neil.springcart.repository.CartRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.mapper.CartMapper;
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
    private final CartMapper cartMapper;

    /**
     * Gets a customer's cart details.
     * @param customerId The customer ID.
     * @return The cart details.
     */
    public CartResponse getCustomerCartDetails(Long customerId) {
        Cart cart = getCartByCustomerId(customerId);
        return cartMapper.mapToResponse(cart);
    }

    /**
     * Adds a product to a customer's cart.
     * @param request A request containing the customer ID and product ID.
     */
    @Transactional
    public void addProductToCart(CartRequest request) {
        Product product = getActiveProduct(request.productId());
        Cart cart = getCartByCustomerId(request.customerId());
        cart.addProduct(product);
        log.info("Product (ID: {}) added to cart (ID: {})", product.getId(),
                cart.getId());
    }

    /**
     * Removes a product from a customer's cart.
     * @param request A request containing the customer ID and product ID.
     */
    @Transactional
    public void removeProductFromCart(CartRequest request) {
        Product product = getActiveProduct(request.productId());
        Cart cart = getCartByCustomerId(request.customerId());
        cart.removeProduct(product);
        log.info("Product (ID: {}) removed from cart (ID: {})", product.getId(),
                cart.getId());
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

    private Cart getCartByCustomerId(Long customerId) {
        // Cart ID will usually be the same as Customer ID but searching the
        // cart by customer ID is safer
        return cartRepository.findByCustomerId(customerId).orElseThrow(() ->
            new BadRequestException("Invalid customer ID")
        );
    }
}
