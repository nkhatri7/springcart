package com.neil.springcart.dto;

import com.neil.springcart.model.Address;
import lombok.Builder;

import java.util.Date;

@Builder
public record OrderSummary(Long id, Date date, Address shippingAddress,
                           int items, double price) {}
