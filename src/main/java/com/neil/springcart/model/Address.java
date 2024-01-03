package com.neil.springcart.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Address {
    private String streetAddress;
    private String suburb;
    private AuState state;
    private int postcode;
    private String country;
}
