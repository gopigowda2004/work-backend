package com.workloghub.controller;

import com.workloghub.dto.AuthRequest;
import com.workloghub.dto.AuthResponse;
import com.workloghub.dto.UserRegisterRequest;
import com.workloghub.entity.User;
import com.workloghub.security.JwtUtil;
import com.workloghub.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        try {
            if (userService.emailExists(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email already exists");
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setFullName(request.getFullName());
            user.setJobTitle(request.getJobTitle());
            user.setCompany(request.getCompany());
            user.setIsJunior(request.getIsJunior() != null ? request.getIsJunior() : false);
            user.setBio(request.getBio());

            User savedUser = userService.createUser(user);

            String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail());

            AuthResponse response = new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getIsJunior()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            User user = userService.findByEmail(request.getEmail());

            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            String token = jwtUtil.generateToken(user.getId(), user.getEmail());

            AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getIsJunior()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }
}
