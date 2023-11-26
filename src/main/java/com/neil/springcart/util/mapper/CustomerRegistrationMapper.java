package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.RegisterRequest;
import com.neil.springcart.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerRegistrationMapper {

    /**
     * Maps the data from the given RegisterRequest object to a Customer object.
     * @param request The body of a request from the /register route.
     * @param password The encrypted customer password.
     * @return A Customer object with the data from the RegisterRequest object.
     */
    public Customer mapToCustomer(RegisterRequest request, String password) {
        return Customer.builder()
                .name(request.name().trim())
                .email(request.email().trim())
                .password(password)
                .build();
    }
}
