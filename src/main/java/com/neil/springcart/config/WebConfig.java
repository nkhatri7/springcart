package com.neil.springcart.config;

import com.neil.springcart.util.converter.StringToProductCategoryConverter;
import com.neil.springcart.util.converter.StringToProductGenderConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToProductGenderConverter());
        registry.addConverter(new StringToProductCategoryConverter());
    }
}
