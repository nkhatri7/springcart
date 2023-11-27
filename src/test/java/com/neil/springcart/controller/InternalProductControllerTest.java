package com.neil.springcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.dto.UpdateProductInventoryRequest;
import com.neil.springcart.dto.UpdateProductRequest;
import com.neil.springcart.model.*;
import com.neil.springcart.repository.AdminRepository;
import com.neil.springcart.repository.CustomerRepository;
import com.neil.springcart.repository.InventoryRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class InternalProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private JwtUtil jwtUtils;

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
        customerRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void handleNewProductCreatesProductInDatabase() throws Exception {
        // When a request is coming from an admin and their JWT token
        Admin admin = createAdmin();
        String token = generateUserToken(admin);
        NewProductRequest request = generateNewProductRequest();
        String requestJson = objectMapper.writeValueAsString(request);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 201 status is returned with the product, and it's inventory
        // items being saved in the database
        mockMvc.perform(MockMvcRequestBuilders.post("/internal/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(requestHeaders)
                        .content(requestJson))
                        .andExpect(status().isCreated());
        assertThat(productRepository.findAll().size()).isEqualTo(1);
        assertThat(inventoryRepository.findAll().size())
                .isEqualTo(request.inventory().size());
    }

    @Test
    void handleNewProductReturns403StatusWhenANonAdminMakesTheRequest()
            throws Exception {
        // When a request is coming from a customer and their JWT token
        Customer customer = createCustomer();
        String token = generateUserToken(customer);
        NewProductRequest request = generateNewProductRequest();
        String requestJson = objectMapper.writeValueAsString(request);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 403 status is returned
        mockMvc.perform(MockMvcRequestBuilders.post("/internal/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(requestHeaders)
                        .content(requestJson))
                        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void handleUpdateProductUpdatesProductNameAndDescription()
            throws Exception {
        // When a request is coming from and admin and their JWT token
        Admin admin = createAdmin();
        String token = generateUserToken(admin);
        String oldName = "old name";
        String oldDescription = "old description";
        String newName = "new name";
        String newDescription = "new description";
        Product product = saveProductToDb(oldName, oldDescription);
        UpdateProductRequest request = generateUpdateProductRequest(newName,
                newDescription);
        String requestJson = objectMapper.writeValueAsString(request);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 200 status is returned with the name and description for the
        // product is updated
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/internal/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(requestHeaders)
                        .content(requestJson))
                        .andExpect(status().isOk());
        Optional<Product> updatedProduct = productRepository
                .findById(product.getId());
        assertThat(updatedProduct.isPresent()).isTrue();
        assertThat(updatedProduct.get().getName()).isEqualTo(newName);
        assertThat(updatedProduct.get().getDescription())
                .isEqualTo(newDescription);
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void handleUpdateProductInventoryUpdatesInventory() throws Exception {
        // When a request is coming from an admin and their JWT token
        Admin admin = createAdmin();
        String token = generateUserToken(admin);
        List<Inventory> productInventory = List.of(
                buildInventory(ProductSize.S, 10)
        );
        Product product = saveProductToDb("name", "description",
                productInventory);
        List<InventoryDto> inventoryDtoList = List.of(
                new InventoryDto(ProductSize.S, 20),
                new InventoryDto(ProductSize.M, 20)
        );
        UpdateProductInventoryRequest request =
                new UpdateProductInventoryRequest(inventoryDtoList);
        String requestJson = objectMapper.writeValueAsString(request);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 200 status is returned with the product inventory being
        // updated
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/internal/products/" + product.getId() + "/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(requestHeaders)
                        .content(requestJson))
                        .andExpect(status().isOk());
        List<Inventory> updatedInventory = inventoryRepository
                .findInventoryByProduct(product.getId());
        assertThat(updatedInventory.size()).isEqualTo(inventoryDtoList.size());
        Inventory smallInventory = updatedInventory.get(0);
        assertThat(smallInventory.getStock()).isEqualTo(20);
        Inventory mediumInventory = updatedInventory.get(1);
        assertThat(mediumInventory.getStock()).isEqualTo(20);
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void handleArchiveProductArchivesProduct() throws Exception {
        // When a request is coming from an admin and their JWT token and
        // the product is not archived
        Admin admin = createAdmin();
        String token = generateUserToken(admin);
        Product product = saveProductToDb("name", "description", true);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 200 status is returned with the product being archived
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/internal/products/" + product.getId() + "/archive")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(requestHeaders))
                .andExpect(status().isOk());
        Optional<Product> updatedProduct = productRepository
                .findById(product.getId());
        updatedProduct.ifPresent(p -> assertThat(p.isActive()).isFalse());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void handleUnarchiveProductUnarchivesProduct() throws Exception {
        // When a request is coming from an admin and their JWT token and
        // the product is archived
        Admin admin = createAdmin();
        String token = generateUserToken(admin);
        Product product = saveProductToDb("name", "description", false);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // Then a 200 status is returned with the product being unarchived
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/internal/products/" + product.getId() + "/unarchive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(requestHeaders))
                .andExpect(status().isOk());
        Optional<Product> updatedProduct = productRepository
                .findById(product.getId());
        updatedProduct.ifPresent(p -> assertThat(p.isActive()).isTrue());
    }

    private String generateUserToken(UserDetails user) {
        return jwtUtils.generateToken(user);
    }

    private Admin createAdmin() {
        Admin admin = Admin.builder()
                .email("admin@springcart.com")
                .password("password")
                .build();
        return adminRepository.save(admin);
    }

    private Customer createCustomer() {
        Customer customer = Customer.builder()
                .name("name")
                .email("notanadmin@springcart.com")
                .password("password")
                .build();
        return customerRepository.save(customer);
    }

    private NewProductRequest generateNewProductRequest() {
        List<InventoryDto> inventoryList = List.of(
                new InventoryDto(ProductSize.S, 5),
                new InventoryDto(ProductSize.L, 10)
        );
        return NewProductRequest.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .inventory(inventoryList)
                .build();
    }

    private UpdateProductRequest generateUpdateProductRequest(String name,
                                                              String description) {
        return UpdateProductRequest.builder()
                .name(name)
                .description(description)
                .build();
    }

    private Product saveProductToDb(String name, String description) {
        Product product = buildProduct(name, description, new ArrayList<>(),
                true);
        return productRepository.save(product);
    }

    private Product saveProductToDb(String name, String description,
                                    List<Inventory> inventoryList) {
        Product product = buildProduct(name, description, inventoryList, true);
        return productRepository.save(product);
    }

    private Product saveProductToDb(String name, String description,
                                    boolean isActive) {
        Product product = buildProduct(name, description, new ArrayList<>(),
                isActive);
        return productRepository.save(product);
    }

    private Product buildProduct(String name, String description,
                                 List<Inventory> inventoryList,
                                 boolean isActive) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .brand("brand")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .isActive(isActive)
                .build();
        for (Inventory inventory : inventoryList) {
            inventory.setProduct(product);
        }
        product.setInventoryList(inventoryList);
        return product;
    }

    private Inventory buildInventory(ProductSize size, int stock) {
        return Inventory.builder()
                .size(size)
                .stock(stock)
                .build();
    }
}