package com.neil.springcart.util.converter;

import com.neil.springcart.model.ProductCategory;
import org.springframework.core.convert.converter.Converter;

public class StringToProductCategoryConverter
        implements Converter<String, ProductCategory> {
    @Override
    public ProductCategory convert(String source) {
        return ProductCategory.valueOf(source.toUpperCase());
    }
}
