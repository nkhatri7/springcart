package com.neil.springcart.util.converter;

import com.neil.springcart.model.ProductGender;
import org.springframework.core.convert.converter.Converter;

public class StringToProductGenderConverter
        implements Converter<String, ProductGender> {
    @Override
    public ProductGender convert(String source) {
        return ProductGender.valueOf(source.toUpperCase());
    }
}
