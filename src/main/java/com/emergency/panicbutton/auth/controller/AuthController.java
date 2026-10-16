package com.emergency.panicbutton.auth.controller;

import com.emergency.panicbutton.auth.dto.LoginRequest;
import com.emergency.panicbutton.auth.dto.RegisterRequest;
import com.emergency.panicbutton.auth.service.AuthService;
import com.emergency.panicbutton.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login users")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public ApiResponse<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "User login with JWT generation")
    public ApiResponse<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}
