package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.CustomerResponse;
import com.neil.springcart.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }
}
