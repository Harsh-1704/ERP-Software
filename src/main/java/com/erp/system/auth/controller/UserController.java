package com.erp.system.auth.controller;

import com.erp.system.auth.dto.UserCreateRequest;
import com.erp.system.auth.entity.User;
import com.erp.system.auth.service.UserService;
import com.erp.system.common.dto.UserDTO;
import com.erp.system.common.mapper.EntityMapper;
import com.erp.system.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final EntityMapper entityMapper;

    @PostMapping("/create")
    @Operation(summary = "Create a new user", description = "Creates a new user with the specified username, password, and role")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ApiResponse<UserDTO.UserResponse> createUser(@RequestBody UserCreateRequest request) {
        User user = userService.createUser(request.getUsername(), request.getPassword(), request.getRole());
        return ApiResponse.success(entityMapper.toUserResponse(user), "User created successfully");
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ApiResponse<List<UserDTO.UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ApiResponse.success(entityMapper.toUserResponseList(users));
    }
}