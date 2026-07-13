package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.requests.LoginRequest;
import com.bd.patientsmd.models.requests.RefreshTokenRequest;
import com.bd.patientsmd.models.responses.AuthResponse;
import com.bd.patientsmd.services.RefreshTokenService;
import com.bd.patientsmd.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsersService usersService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UsersService usersService, RefreshTokenService refreshTokenService) {
        this.usersService = usersService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return usersService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.refreshAccessToken(request.refreshToken());
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.refreshToken());
    }
}
