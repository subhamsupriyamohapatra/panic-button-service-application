package com.emergency.panicbutton.user.controller;

import com.emergency.panicbutton.common.ApiResponse;
import com.emergency.panicbutton.config.JwtAuthFilter;
import com.emergency.panicbutton.user.dto.UserCreate;
import com.emergency.panicbutton.user.dto.UserResponse;
import com.emergency.panicbutton.user.dto.UserUpdate;
import com.emergency.panicbutton.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "HTTPBearer")
@Tag(name = "Users", description = "Manage users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ApiResponse<UserResponse> me(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute(JwtAuthFilter.AUTH_USER_ATTRIBUTE);
        return ApiResponse.ok(userService.getById(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user (administrative)")
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreate body) {
        return ApiResponse.ok(userService.create(body));
    }

    @GetMapping
    @Operation(summary = "List all users")
    public ApiResponse<List<UserResponse>> list(
            @RequestParam(defaultValue = "0") @Min(0) int skip,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int limit) {
        return ApiResponse.ok(userService.list(skip, limit));
    }

    @GetMapping("/{user_id}")
    @Operation(summary = "Get user by ID")
    public ApiResponse<UserResponse> getById(@PathVariable("user_id") UUID userId) {
        return ApiResponse.ok(userService.getById(userId));
    }

    @PutMapping("/{user_id}")
    @Operation(summary = "Update user details")
    public ApiResponse<UserResponse> update(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody UserUpdate body) {
        return ApiResponse.ok(userService.update(userId, body));
    }

    @DeleteMapping("/{user_id}")
    @Operation(summary = "Delete user by ID")
    public ApiResponse<Object> delete(@PathVariable("user_id") UUID userId) {
        userService.delete(userId);
        return ApiResponse.ok(null, "User deleted successfully");
    }
}
