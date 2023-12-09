package com.neil.springcart.service;

import com.neil.springcart.dto.AddInventoryRequest;
import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.exception.BadRequestException;
import com.neil.springcart.exception.NotFoundException;
import com.neil.springcart.model.InventoryItem;
import com.neil.springcart.model.Product;
import com.neil.springcart.repository.InventoryRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.mapper.InventoryMapper;
import com.neil.springcart.util.mapper.NewProductMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        productRepository.save(product);
        saveProductInventory(product, request.inventory());
        return product;
    }

    /**
     * Adds inventory for the product with the given ID.
     * @param productId The product ID.
     * @param request The request containing the new inventory data.
     */
    public void addProductInventory(Long productId,
                                    AddInventoryRequest request) {
        Product product = getActiveProduct(productId);
        saveProductInventory(product, request.inventory());
    }

    /**
     * Creates inventory items in the database for the given product.
     * @param product The product the inventory is for.
     * @param inventoryDtoList The incoming inventory data (i.e. size and
     *                         additional stock).
     */
    private void saveProductInventory(Product product,
                                      List<InventoryDto> inventoryDtoList) {
        List<InventoryItem> inventory = inventoryMapper.mapToInventory(product,
                inventoryDtoList);
        inventoryRepository.saveAll(inventory);
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
