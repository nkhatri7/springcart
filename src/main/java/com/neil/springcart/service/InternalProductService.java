package com.neil.springcart.service;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.exception.NotFoundException;
import com.neil.springcart.model.Inventory;
import com.neil.springcart.model.Product;
import com.neil.springcart.repository.InventoryRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.mapper.InventoryMapper;
import com.neil.springcart.util.mapper.NewProductMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InternalProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final NewProductMapper newProductMapper;
    private final InventoryMapper inventoryMapper;

    /**
     * Saves a product with the details from the request in the database.
     * @param request The request containing the product details.
     * @return The created product.
     */
    public Product createProduct(NewProductRequest request) {
        Product product = newProductMapper.mapToProduct(request);
        return productRepository.save(product);
    }

    /**
     * Gets the product with the given ID from the database if one exists.
     * @param id The ID of the product.
     * @return The product data for the product with the given ID if it exists.
     * @throws NotFoundException If a product with the given ID doesn't exist.
     */
    private Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Product with ID " + id + " does not exist")
        );
    }

    /**
     * Gets the product with the given ID from the database if it exists and if
     * it's active.
     * @param id The ID of the product.
     * @return The product data for the product with the given ID if it exists
     * and is active.
     * @throws BadRequestException If the product with the given ID isn't
     * active.
     */
    private Product getActiveProduct(Long id) {
        Product product = getProduct(id);
        if (!product.isActive()) {
            throw new BadRequestException("Product is not active");
        }
        return product;
    }

    /**
     * Updates product details for the given product with data from the given
     * request.
     * @param productId the ID of the product to be updated.
     * @param request The updated data.
     */
    @Transactional
    public void updateProduct(Long productId, UpdateProductRequest request) {
        Product product = getActiveProduct(productId);
        if (request.name() != null && canUpdateValue(product.getName(),
                request.name())) {
            product.setName(request.name().trim());
        }

        if (request.description() != null && canUpdateValue(
                product.getDescription(), request.description())) {
            product.setDescription(request.description().trim());
        }
    }

    /**
     * Checks if a property can be updated by checking if the new value is not
     * equal to the previous value.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     * @return {@code true} if the value can be updated, {@code false}
     * otherwise.
     */
    private boolean canUpdateValue(String oldValue, String newValue) {
        return !newValue.trim().isEmpty()
                && !oldValue.trim().equals(newValue.trim());
    }

    /**
     * Updates the inventory for the given product in the database.
     * @param productId The product ID of which the inventory should be updated
     * for.
     * @param inventoryDtoList The updated inventory data.
     */
    public void updateProductInventory(Long productId,
                                       List<InventoryDto> inventoryDtoList) {
        Product product = getActiveProduct(productId);
        List<Inventory> productInventory = inventoryRepository
                .findInventoryByProduct(product.getId());
        List<Inventory> inventoryList = inventoryDtoList.stream()
                .map(dto -> inventoryMapper.mapToInventory(dto, product))
                .toList();
        for (Inventory inventory : inventoryList) {
            saveInventoryItem(inventory, productInventory);
        }
    }

    /**
     * Saves the given inventory item to the database.
     * @param inventory An inventory item for a product.
     * @param productInventory The existing product inventory list.
     */
    @Transactional
    private void saveInventoryItem(Inventory inventory,
                                   List<Inventory> productInventory) {
        Optional<Inventory> existingInventory = getExistingInventoryItem(
                inventory, productInventory);
        // Check if inventory item exists and update it, otherwise create
        // new inventory item for product
        if (existingInventory.isPresent()) {
            existingInventory.get().setStock(inventory.getStock());
        } else {
            inventoryRepository.save(inventory);
        }
    }

    /**
     * Gets the matching inventory item from the existing product inventory if
     * it exists.
     * @param inventory The inventory item to be added.
     * @param productInventory The existing product inventory list.
     * @return {@code true} if the inventory list contains an item with the same
     * size as the given inventory item, {@code false} otherwise.
     */
    private Optional<Inventory> getExistingInventoryItem(Inventory inventory,
                                             List<Inventory> productInventory) {
        return productInventory.stream()
                .filter(i -> i.getSize().equals(inventory.getSize()))
                .findFirst();
    }

    /**
     * Archives a product.
     * @param productId The ID of the product.
     * @throws BadRequestException If the product is already archived.
     */
    @Transactional
    public void archiveProduct(Long productId) {
        Product product = getProduct(productId);
        if (!product.isActive()) {
            throw new BadRequestException("Product is already archived");
        }
        product.setActive(false);
    }

    /**
     * Unarchives a product.
     * @param productId The ID of the product.
     * @throws BadRequestException If the product is already active.
     */
    @Transactional
    public void unarchiveProduct(Long productId) {
        Product product = getProduct(productId);
        if (product.isActive()) {
            throw new BadRequestException("Product is already active");
        }
        product.setActive(true);
    }
}
