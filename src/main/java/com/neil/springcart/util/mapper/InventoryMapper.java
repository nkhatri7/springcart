package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.model.InventoryItem;
import com.neil.springcart.model.Product;
import com.neil.springcart.model.ProductSize;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class InventoryMapper {
    public List<InventoryItem> mapToInventory(Product product,
                                              List<InventoryDto> inventoryDtoList) {
        return inventoryDtoList.stream()
                .flatMap(inventoryDto -> createSizeInventoryItems(product,
                        inventoryDto))
                .toList();
    }

    private Stream<InventoryItem> createSizeInventoryItems(Product product,
                                                           InventoryDto dto) {
        return IntStream.range(0, dto.stock())
                .mapToObj(n -> mapToInventoryItem(product, dto.size()));
    }

    public InventoryItem mapToInventoryItem(Product product, ProductSize size) {
        return InventoryItem.builder()
                .product(product)
                .size(size)
                .isSold(false)
                .build();
    }

    public List<InventoryDto> mapToDtoList(List<InventoryItem> inventory) {
        // Get product size count
        Map<ProductSize, Long> productSizeCount = inventory.stream()
                .collect(Collectors.groupingBy(InventoryItem::getSize,
                        Collectors.counting()));
        // Map to InventoryDto
        return productSizeCount.entrySet().stream()
                .map(entry -> new InventoryDto(entry.getKey(),
                        Math.toIntExact(entry.getValue())))
                .toList();
    }
}
