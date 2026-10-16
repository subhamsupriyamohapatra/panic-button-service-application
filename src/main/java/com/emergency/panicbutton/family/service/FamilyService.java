package com.emergency.panicbutton.family.service;

import com.emergency.panicbutton.family.dto.FamilyMappingResponse;
import com.emergency.panicbutton.family.dto.FamilyMemberCreate;
import com.emergency.panicbutton.family.dto.FamilyMemberResponse;
import com.emergency.panicbutton.family.dto.FamilyMemberUpdate;
import com.emergency.panicbutton.family.entity.FamilyMapping;
import com.emergency.panicbutton.family.repository.FamilyMappingRepository;
import com.emergency.panicbutton.user.entity.User;
import com.emergency.panicbutton.user.repository.UserRepository;
import com.emergency.panicbutton.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyMappingRepository familyMappingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public FamilyMappingResponse addFamilyMember(UUID userId, FamilyMemberCreate request) {
        userService.getUser(userId);

        User familyMember = userService.createEntity(
                request.getName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getPassword(),
                "ACTIVE");

        FamilyMapping mapping = new FamilyMapping();
        mapping.setUser(userRepository.getReferenceById(userId));
        mapping.setFamilyMember(familyMember);
        mapping.setRelationship(request.getRelationship());
        mapping.setStatus("ACTIVE");

        return toResponse(familyMappingRepository.save(mapping));
    }

    @Transactional(readOnly = true)
    public List<FamilyMappingResponse> listFamilyMembers(UUID userId) {
        userService.getUser(userId);
        return familyMappingRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FamilyMemberResponse updateFamilyMember(
            UUID userId, UUID familyMemberId, FamilyMemberUpdate request) {

        FamilyMapping mapping = resolveMapping(userId, familyMemberId);
        User member = mapping.getFamilyMember();

        if (request.getName() != null) {
            member.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            member.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            String normalized = request.getEmail().trim().toLowerCase();
            if (!normalized.equals(member.getEmail())
                    && userRepository.existsByEmail(normalized)) {
                throw new IllegalArgumentException("Email already registered");
            }
            member.setEmail(normalized);
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            member.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getStatus() != null) {
            member.setStatus(request.getStatus());
        }

        return toMemberResponse(userRepository.save(member));
    }

    @Transactional
    public void removeFamilyMember(UUID userId, UUID familyMemberId) {
        FamilyMapping mapping = resolveMapping(userId, familyMemberId);
        familyMappingRepository.delete(mapping);
    }

    private FamilyMapping resolveMapping(UUID userId, UUID familyMemberId) {
        List<FamilyMapping> mappings = familyMappingRepository
                .findByUserIdAndFamilyMemberId(userId, familyMemberId);
        if (mappings.isEmpty()) {
            throw new NoSuchElementException("Family member not found for this user");
        }
        return mappings.get(0);
    }

    private FamilyMappingResponse toResponse(FamilyMapping mapping) {
        return new FamilyMappingResponse(
                mapping.getId(),
                mapping.getUser().getId(),
                mapping.getFamilyMember().getId(),
                mapping.getRelationship(),
                mapping.getStatus(),
                mapping.getCreatedAt(),
                toMemberResponse(mapping.getFamilyMember()));
    }

    private FamilyMemberResponse toMemberResponse(User member) {
        return new FamilyMemberResponse(
                member.getId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getEmail(),
                member.getStatus(),
                member.getCreatedAt());
    }
}
