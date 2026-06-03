package com.erp.system.auth.controller;

import com.erp.system.auth.dto.LoginRequest;
import com.erp.system.auth.dto.RegisterRequest;
import com.erp.system.auth.entity.User;
import com.erp.system.auth.service.UserService;
import com.erp.system.common.response.ApiResponse;
import com.erp.system.config.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {

        User user = userService.getUserByUsername(request.getUsername());

        if (user == null) {
            return ApiResponse.error("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error("Invalid password");
        }

        return ApiResponse.success(jwtService.generateToken(user.getUsername()), "Login successful");
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {

        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getRole()
            );
            // Auto-generate JWT so frontend can log in immediately after registration
            String token = jwtService.generateToken(user.getUsername());
            return ApiResponse.success(token, "Registration successful");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}