package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.model.Inventory;
import com.neil.springcart.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class NewProductMapper {
    private final InventoryMapper inventoryMapper;

    public Product mapToProduct(NewProductRequest request) {
        Product product = Product.builder()
                .brand(request.brand().trim())
                .name(request.name().trim())
                .description(request.description().trim())
                .category(request.category())
                .gender(request.gender())
                .isActive(true)
                .build();
        addInventoryToProduct(request.inventory(), product);
        return product;
    }

    private void addInventoryToProduct(List<InventoryDto> inventoryDtoList,
                                       Product product) {
        List<Inventory> inventoryList = inventoryDtoList.stream()
                .map(dto -> inventoryMapper.mapToInventory(dto, product))
                .toList();
        product.setInventoryList(inventoryList);
    }
}
