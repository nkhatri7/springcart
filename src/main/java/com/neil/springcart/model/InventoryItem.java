package com.neil.springcart.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "InventoryItem")
@Table(name = "inventory")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
    @Id
    @SequenceGenerator(
            name = "inventory_sequence",
            sequenceName = "inventory_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "inventory_sequence"
    )
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductSize size;
    @Column(nullable = false)
    private boolean isSold;
}
