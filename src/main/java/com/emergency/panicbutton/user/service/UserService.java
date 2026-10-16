package com.emergency.panicbutton.user.service;

import com.emergency.panicbutton.common.OffsetBasedPageRequest;
import com.emergency.panicbutton.user.dto.UserCreate;
import com.emergency.panicbutton.user.dto.UserResponse;
import com.emergency.panicbutton.user.dto.UserUpdate;
import com.emergency.panicbutton.user.entity.User;
import com.emergency.panicbutton.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createEntity(
            String name, String email, String phoneNumber,
            String rawPassword, String status) {

        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(normalizedEmail);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setStatus(status == null ? "ACTIVE" : status);

        return userRepository.save(user);
    }

    @Transactional
    public UserResponse create(UserCreate request) {
        User user = createEntity(
                request.getName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getPassword(),
                request.getStatus());
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list(int skip, int limit) {
        return userRepository
                .findAll(new OffsetBasedPageRequest(skip, limit, Sort.unsorted()))
                .map(this::toResponse)
                .getContent();
    }

    @Transactional(readOnly = true)
    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return toResponse(getUser(id));
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdate request) {
        User user = getUser(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            String normalized = request.getEmail().trim().toLowerCase();
            if (!normalized.equals(user.getEmail())
                    && userRepository.existsByEmail(normalized)) {
                throw new IllegalArgumentException("Email already registered");
            }
            user.setEmail(normalized);
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
