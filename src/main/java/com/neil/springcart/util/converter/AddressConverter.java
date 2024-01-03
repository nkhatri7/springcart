package com.neil.springcart.util.converter;

import com.neil.springcart.model.Address;
import com.neil.springcart.model.AuState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AddressConverter implements AttributeConverter<Address, String> {

    @Override
    public String convertToDatabaseColumn(Address address) {
        return String.format("%s, %s, %s, %d, %s", address.getStreetAddress(),
                address.getSuburb(), address.getState(), address.getPostcode(),
                address.getCountry());
    }

    @Override
    public Address convertToEntityAttribute(String addressString) {
        String[] parts = addressString.split(", ");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid address string format");
        }

        return Address.builder()
                .streetAddress(parts[0])
                .suburb(parts[1])
                .state(AuState.valueOf(parts[2]))
                .postcode(Integer.parseInt(parts[3]))
                .country(parts[4])
                .build();
    }
}
