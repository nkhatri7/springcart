package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.model.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class NewProductMapper {
    public Product mapToProduct(NewProductRequest request) {
        return Product.builder()
                .brand(request.brand().trim())
                .name(request.name().trim())
                .description(request.description().trim())
                .category(request.category())
                .gender(request.gender())
                .price(request.price())
                .inventory(new ArrayList<>())
                .isActive(true)
                .build();
    }
}
