package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.CartResponse;
import com.neil.springcart.model.Cart;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CartMapper {
    private final ProductMapper productMapper;

    public CartResponse mapToResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .items(productMapper.mapListToResponse(cart.getProducts()))
                .build();
    }
}
