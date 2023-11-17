package com.neil.springcart.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "Product")
@Table(name = "product")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @SequenceGenerator(
            name = "product_sequence",
            sequenceName = "product_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_sequence"
    )
    private Long id;
    @Column(nullable = false, unique = true)
    private UUID sku;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductGender gender;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Inventory> inventoryList;

    // Generate random UUID for SKU
    // https://stackoverflow.com/questions/62777718/how-to-auto-generate-uuid-value-for-non-primary-key-using-jpa
    @PrePersist
    protected void onCreate() {
        setSku(UUID.randomUUID());
    }
}
