package com.emergency.panicbutton.family.controller;

import com.emergency.panicbutton.common.ApiResponse;
import com.emergency.panicbutton.family.dto.FamilyMappingResponse;
import com.emergency.panicbutton.family.dto.FamilyMemberCreate;
import com.emergency.panicbutton.family.dto.FamilyMemberResponse;
import com.emergency.panicbutton.family.dto.FamilyMemberUpdate;
import com.emergency.panicbutton.family.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{user_id}/family-members")
@RequiredArgsConstructor
@SecurityRequirement(name = "HTTPBearer")
@Tag(name = "Family Management",
        description = "Manage a user's emergency family circle")
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a family member - creates their account and links them")
    public ApiResponse<FamilyMappingResponse> addFamilyMember(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody FamilyMemberCreate request) {
        return ApiResponse.ok(familyService.addFamilyMember(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all family members linked to a user")
    public ApiResponse<List<FamilyMappingResponse>> listFamilyMembers(
            @PathVariable("user_id") UUID userId) {
        return ApiResponse.ok(familyService.listFamilyMembers(userId));
    }

    @PutMapping("/{family_member_id}")
    @Operation(summary = "Update a family member's account details")
    public ApiResponse<FamilyMemberResponse> updateFamilyMember(
            @PathVariable("user_id") UUID userId,
            @PathVariable("family_member_id") UUID familyMemberId,
            @Valid @RequestBody FamilyMemberUpdate request) {
        return ApiResponse.ok(
                familyService.updateFamilyMember(userId, familyMemberId, request));
    }

    @DeleteMapping("/{family_member_id}")
    @Operation(summary = "Remove a family member from your emergency circle")
    public ApiResponse<Object> removeFamilyMember(
            @PathVariable("user_id") UUID userId,
            @PathVariable("family_member_id") UUID familyMemberId) {
        familyService.removeFamilyMember(userId, familyMemberId);
        return ApiResponse.ok(null, "Family member removed successfully");
    }
}
