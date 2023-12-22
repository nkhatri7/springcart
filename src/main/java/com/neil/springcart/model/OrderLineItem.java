package com.neil.springcart.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "OrderLineItem")
@Table(name = "order_line_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItem {
    @Id
    @SequenceGenerator(
            name = "order_line_item_sequence",
            sequenceName = "order_line_item_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_line_item_sequence"
    )
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private ProductSize size;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "inventory_item_id", unique = true)
    private InventoryItem inventoryItem;
    @Column(nullable = false)
    private boolean isReturned;
}
