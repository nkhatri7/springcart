package com.neil.springcart.service;

import com.neil.springcart.dto.InventoryDto;
import com.neil.springcart.dto.NewProductRequest;
import com.neil.springcart.model.Admin;
import com.neil.springcart.model.ProductCategory;
import com.neil.springcart.model.ProductGender;
import com.neil.springcart.model.ProductSize;
import com.neil.springcart.repository.AdminRepository;
import com.neil.springcart.repository.InventoryRepository;
import com.neil.springcart.repository.ProductRepository;
import com.neil.springcart.security.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class InternalProductServiceTest {
    private InternalProductService internalProductService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        internalProductService = new InternalProductService(productRepository,
                inventoryRepository, adminRepository, jwtUtils);
    }

    @AfterEach
    void tearDown() {
        reset(productRepository, inventoryRepository, adminRepository,
                jwtUtils);
    }

    @Test
    void isAdminReturnsTrueWhenTokenIsLinkedToAdminAccount() {
        // Given the token is linked to an admin account
        String email = "admin@springcart.com";
        Admin admin = createAdmin(email);
        given(adminRepository.findByEmail(email))
                .willReturn(Optional.of(admin));
        String token = "token";
        given(jwtUtils.extractUsername(token)).willReturn(admin.getUsername());
        String authHeader = "Bearer " + token;
        // Then isAdmin() will return true
        assertThat(internalProductService.isAdmin(authHeader)).isTrue();
    }

    @Test
    void isAdminReturnsFalseWhenTokenIsNotLinkedToAdminAccount() {
        // Given the token is not linked to an admin account
        String token = "token";
        given(jwtUtils.extractUsername(token)).willReturn("notanadmin");
        String authHeader = "Bearer " + token;
        // Then isAdmin() will return true
        assertThat(internalProductService.isAdmin(authHeader)).isFalse();
    }

    @Test
    void createProductSavesProductOnce() {
        NewProductRequest request = buildNewProductRequest(new ArrayList<>());
        internalProductService.createProduct(request);
        verify(productRepository, times(1)).save(any());
    }

    @Test
    void createProductSaves2InventoryItemsWhenThereAre2InventoryItemsInTheRequest() {
        List<InventoryDto> inventoryList = List.of(
                new InventoryDto(ProductSize.S, 5),
                new InventoryDto(ProductSize.L, 10)
        );
        NewProductRequest request = buildNewProductRequest(inventoryList);
        internalProductService.createProduct(request);
        verify(inventoryRepository, times(request.inventory().size()))
                .save(any());
    }

    private NewProductRequest buildNewProductRequest(
            List<InventoryDto> inventoryList) {
        return NewProductRequest.builder()
                .brand("brand")
                .name("name")
                .description("description")
                .category(ProductCategory.SPORTSWEAR)
                .gender(ProductGender.MALE)
                .inventory(inventoryList)
                .build();
    }

    private Admin createAdmin(String email) {
        return Admin.builder()
                .email(email)
                .password("password")
                .build();
    }
}