package com.neil.springcart.service;

import com.neil.springcart.dto.CartRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.CartRepository;
import com.neil.springcart.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CartServiceTest {
    private CartService cartService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, productRepository);
    }

    @AfterEach
    void tearDown() {
        reset(cartRepository, productRepository);
    }

    @Test
    void addProductToCartShouldThrowABadRequestExceptionIfTheProductIsAlreadyInTheCart() {
        // Given that the product with ID 1 is already in the cart with ID 1
        Customer customer = buildCustomer();
        Long productId = 1L;
        Product product = buildProduct(productId);
        Cart cart = buildCart(customer, List.of(product));
        customer.setCart(cart);
        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));
        given(cartRepository.findById(cart.getId()))
                .willReturn(Optional.of(cart));

        // When addProductToCart() is called with customer ID 1 and product ID 1
        CartRequest request = new CartRequest(customer.getId(), productId);

        // Then a BadRequestException is thrown
        assertThrows(BadRequestException.class, () -> {
            cartService.addProductToCart(request);
        });
    }

    @Test
    void addProductToCartShouldAddProductToCartIfItIsNotAlreadyInCart() {
        // Given that the product with ID is not already in the cart with ID 1
        Customer customer = buildCustomer();
        Cart cart = buildCart(customer, new ArrayList<>());
        Cart cartSpy = spy(cart);
        customer.setCart(cart);
        Long productId = 1L;
        Product product = buildProduct(productId);
        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));
        given(cartRepository.findById(cart.getId()))
                .willReturn(Optional.of(cartSpy));

        // When addProductToCart() is called with customer ID 1 and product ID 1
        CartRequest request = new CartRequest(customer.getId(), productId);
        cartService.addProductToCart(request);

        // Then the product is added to the cart
        verify(cartSpy, times(1)).addProduct(product);
    }

    @Test
    void removeProductFromCartShouldThrowBadRequestExceptionIfProductWasNotInCart() {
        // Given that a product with ID is not in the cart with ID 1
        Customer customer = buildCustomer();
        Cart cart = buildCart(customer, new ArrayList<>());
        Cart cartSpy = spy(cart);
        customer.setCart(cart);
        Long productId = 1L;
        Product product = buildProduct(productId);
        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));
        given(cartRepository.findById(cart.getId()))
                .willReturn(Optional.of(cartSpy));

        // When removeProductFromCart() is called with customer ID 1 and product
        // ID 1
        CartRequest request = new CartRequest(customer.getId(), productId);

        // Then a BadRequestException is thrown
        assertThrows(BadRequestException.class, () -> {
            cartService.removeProductFromCart(request);
        });
    }

    @Test
    void removeProductFromCartShouldRemoveProductFromCartIfItIsInTheCart() {
        // Given that the product with ID 1 is already in the cart with ID 1
        Customer customer = buildCustomer();
        Long productId = 1L;
        Product product = buildProduct(productId);
        // Create immutable list of products so it can be removed
        List<Product> products = new ArrayList<>();
        products.add(product);
        Cart cart = buildCart(customer, products);
        Cart cartSpy = spy(cart);
        customer.setCart(cart);
        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));
        given(cartRepository.findById(cart.getId()))
                .willReturn(Optional.of(cartSpy));

        // When removeProductFromCart() is called with customer ID 1 and product
        // ID 1
        CartRequest request = new CartRequest(customer.getId(), productId);
        cartService.removeProductFromCart(request);

        // Then the product is removed from the cart
        verify(cartSpy, times(1)).removeProduct(product);
    }

    private Cart buildCart(Customer customer, List<Product> products) {
        return Cart.builder()
                .id(customer.getId())
                .products(products)
                .customer(customer)
                .build();
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .id(1L)
                .name("name")
                .email("email")
                .password("password")
                .build();
    }

    private Product buildProduct(Long id) {
        return Product.builder()
                .id(id)
                .sku(UUID.randomUUID())
                .brand("brand")
                .name("name")
                .description("description")
                .gender(ProductGender.UNISEX)
                .category(ProductCategory.SPORTSWEAR)
                .price(50)
                .inventory(new ArrayList<>())
                .isActive(true)
                .build();
    }
}