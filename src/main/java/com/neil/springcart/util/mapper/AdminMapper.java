package com.neil.springcart.util.mapper;

import com.neil.springcart.dto.AdminAuthResponse;
import com.neil.springcart.model.Admin;

public class AdminMapper {
    public AdminAuthResponse mapToResponse(Admin admin) {
        return new AdminAuthResponse(admin.getEmail());
    }
}
