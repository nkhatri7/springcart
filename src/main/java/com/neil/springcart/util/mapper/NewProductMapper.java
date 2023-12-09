package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class NewProductMapper {
    public Product mapToProduct(NewProductRequest request) {
        return Product.builder()
                .brand(request.brand().trim())
                .name(request.name().trim())
                .description(request.description().trim())
                .category(request.category())
                .gender(request.gender())
                .inventory(new ArrayList<>())
                .isActive(true)
                .build();
    }
}
