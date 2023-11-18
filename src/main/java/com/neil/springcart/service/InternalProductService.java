package com.neil.springcart.service;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.model.Admin;
import com.neil.springcart.model.Inventory;
import com.neil.springcart.model.Product;
import com.neil.springcart.repository.AdminRepository;
import com.neil.springcart.repository.InventoryRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.security.JwtUtils;
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
     */
    public void createProduct(NewProductRequest request) {
        Product product = mapNewProductRequestToProduct(request);
        Product newProduct = productRepository.save(product);
        saveProductInventory(newProduct, request.inventory());
    }

    /**
     * Saves the inventory for the given product in the database.
     * @param product The product for which the inventory is assigned to.
     * @param inventoryDtoList The DTO list of inventory items.
     */
    private void saveProductInventory(Product product,
                                      List<InventoryDto> inventoryDtoList) {
        for (InventoryDto inventoryDto : inventoryDtoList) {
            Inventory inventory = mapInventoryDtoToInventory(inventoryDto,
                    product);
            inventoryRepository.save(inventory);
        }
    }

    private Product mapNewProductRequestToProduct(NewProductRequest request) {
        return Product.builder()
                .brand(request.brand().trim())
                .name(request.name().trim())
                .description(request.description().trim())
                .category(request.category())
                .gender(request.gender())
                .isActive(true)
                .build();
    }

    private Inventory mapInventoryDtoToInventory(InventoryDto inventoryDto,
                                                 Product product) {
        return Inventory.builder()
                .product(product)
                .size(inventoryDto.size())
                .stock(inventoryDto.stock())
                .build();
    }
}
