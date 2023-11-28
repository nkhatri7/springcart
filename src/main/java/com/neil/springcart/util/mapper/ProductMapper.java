package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.ProductResponse;
import com.neil.springcart.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .brand(product.getBrand())
                .name(product.getName())
                .gender(product.getGender())
                .category(product.getCategory())
                .build();
    }
}
