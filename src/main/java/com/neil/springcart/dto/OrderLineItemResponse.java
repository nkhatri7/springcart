package com.neil.springcart.dto;

import com.neil.springcart.model.ProductSize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class OrderLineItemResponse {
    private Long id;
    private Long productId;
    private String brand;
    private String name;
    private ProductSize size;
    private double price;
    @Setter
    private int quantity;
}
