package com.jobportal.controller;

import com.jobportal.dto.AuthDTO;
import com.jobportal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthDTO.LoginResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        AuthDTO.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/candidate")
    @Operation(summary = "Register as candidate")
    public ResponseEntity<AuthDTO.LoginResponse> registerCandidate(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        AuthDTO.LoginResponse response = authService.registerCandidate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/recruiter")
    @Operation(summary = "Register as recruiter")
    public ResponseEntity<AuthDTO.LoginResponse> registerRecruiter(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        AuthDTO.LoginResponse response = authService.registerRecruiter(request);
        return ResponseEntity.ok(response);
    }
}
