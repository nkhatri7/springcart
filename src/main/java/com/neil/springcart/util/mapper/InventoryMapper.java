package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.model.Inventory;
import com.neil.springcart.model.Product;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    public Inventory mapToInventory(InventoryDto inventoryDto,
                                    Product product) {
        return Inventory.builder()
                .product(product)
                .size(inventoryDto.size())
                .stock(inventoryDto.stock())
                .build();
    }
}
