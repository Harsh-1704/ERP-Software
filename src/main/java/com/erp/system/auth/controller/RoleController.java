package com.erp.system.auth.controller;

import com.erp.system.auth.entity.Role;
import com.erp.system.auth.service.RoleService;
import com.erp.system.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Create new role
    @PostMapping ("/create")
    public ApiResponse<Role> createRole(@RequestParam String name) {
        return ApiResponse.success(roleService.createRole(name), "Role created successfully");
    }


}