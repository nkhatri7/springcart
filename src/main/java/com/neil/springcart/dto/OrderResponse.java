package com.neil.springcart.dto;

import com.neil.springcart.model.Address;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record OrderResponse(Long id, Date date, Address shippingAddress,
                            double price, List<OrderLineItemResponse> items) {}
