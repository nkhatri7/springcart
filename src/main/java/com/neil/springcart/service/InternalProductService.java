package com.neil.springcart.service;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.model.Admin;
import com.neil.springcart.model.Inventory;
import com.neil.springcart.model.Product;
import com.neil.springcart.repository.AdminRepository;
import com.neil.springcart.repository.InventoryRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.security.JwtUtils;
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
    private final AdminRepository adminRepository;
    private final JwtUtils jwtUtils;

    /**
     * Checks if the user making the request is an admin.
     * @param authHeader The Authorization header value from the request.
     * @return {@code true} if the request is from an admin, {@code false}
     * otherwise.
     */
    public boolean isAdmin(String authHeader) {
        String authToken = authHeader.substring(7);
        String email = jwtUtils.extractUsername(authToken);
        Optional<Admin> admin = adminRepository.findByEmail(email);
        return admin.isPresent();
    }

    /**
     * Saves a product with the details from the request in the database.
     * @param request The request containing the product details.
     * @return The created product.
     */
    public Product createProduct(NewProductRequest request) {
        Product product = mapNewProductRequestToProduct(request);
        return productRepository.save(product);
    }

    private Product mapNewProductRequestToProduct(NewProductRequest request) {
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
                .map(dto -> mapInventoryDtoToInventory(dto, product))
                .toList();
        product.setInventoryList(inventoryList);
    }

    private Inventory mapInventoryDtoToInventory(InventoryDto inventoryDto,
                                                 Product product) {
        return Inventory.builder()
                .product(product)
                .size(inventoryDto.size())
                .stock(inventoryDto.stock())
                .build();
    }

    /**
     * Gets the product with the given ID from the database if one exists.
     * @param id The ID of the product.
     * @return An optional product object which is empty is a product with the
     * given ID does not exist.
     */
    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Updates product details for the given product with data from the given
     * request.
     * @param product The product being updated.
     * @param request The updated data.
     */
    @Transactional
    public void updateProduct(Product product, UpdateProductRequest request) {
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
}
