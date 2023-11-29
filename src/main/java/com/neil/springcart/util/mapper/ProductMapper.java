package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.DetailedProductResponse;
import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.ProductResponse;
import com.neil.springcart.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ProductMapper {
    private final InventoryMapper inventoryMapper;

    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .brand(product.getBrand())
                .name(product.getName())
                .gender(product.getGender())
                .category(product.getCategory())
                .build();
    }

    public List<ProductResponse> mapListToResponse(List<Product> products) {
        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DetailedProductResponse mapToDetailedResponse(Product product) {
        List<InventoryDto> inventoryList = product.getInventoryList().stream()
                .map(inventoryMapper::mapToDto)
                .toList();
        return DetailedProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .brand(product.getBrand())
                .name(product.getName())
                .description(product.getDescription())
                .gender(product.getGender())
                .category(product.getCategory())
                .inventory(inventoryList)
                .build();
    }
}
