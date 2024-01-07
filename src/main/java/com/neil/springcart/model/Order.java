package com.neil.springcart.model;

import com.neil.springcart.util.converter.AddressConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity(name = "Order")
@Table(name = "sc_order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @SequenceGenerator(
            name = "order_sequence",
            sequenceName = "order_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_sequence"
    )
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<OrderLineItem> items;
    @Column(nullable = false)
    private Date date;
    @Convert(converter = AddressConverter.class)
    @Column(nullable = false)
    private Address shippingAddress;
    @Column(nullable = false)
    private boolean isCancelled;

    public double getTotalAmount() {
        return this.items.stream()
                .mapToDouble(item -> item.getProduct().getPrice())
                .sum();
    }
}
