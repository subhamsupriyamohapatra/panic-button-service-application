package com.emergency.panicbutton.auth.service;

import com.emergency.panicbutton.auth.dto.LoginRequest;
import com.emergency.panicbutton.auth.dto.RegisterRequest;
import com.emergency.panicbutton.config.JwtService;
import com.emergency.panicbutton.user.dto.UserResponse;
import com.emergency.panicbutton.user.entity.User;
import com.emergency.panicbutton.user.repository.UserRepository;
import com.emergency.panicbutton.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        User user = userService.createEntity(
                request.getName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getPassword(),
                "ACTIVE");

        return tokenPayload(user);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return tokenPayload(user);
    }

    private Map<String, Object> tokenPayload(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        UserResponse userResponse = userService.toResponse(user);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("access_token", token);
        data.put("token_type", "bearer");
        data.put("user", userResponse);
        return data;
    }
}
