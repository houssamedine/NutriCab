package com.bd.patientsmd.controllers;

import com.bd.patientsmd.exceptions.InvalidCredentialsException;
import com.bd.patientsmd.models.requests.LoginRequest;
import com.bd.patientsmd.models.requests.RefreshTokenRequest;
import com.bd.patientsmd.models.responses.AuthResponse;
import com.bd.patientsmd.models.responses.AuthSession;
import com.bd.patientsmd.services.RefreshTokenService;
import com.bd.patientsmd.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final UsersService usersService;
    private final RefreshTokenService refreshTokenService;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(
            UsersService usersService,
            RefreshTokenService refreshTokenService,
            @Value("${app.security.jwt.expiration-minutes}") long accessTokenExpirationMinutes,
            @Value("${app.security.jwt.refresh-expiration-days}") long refreshTokenExpirationDays,
            @Value("${app.security.cookies.secure:false}") boolean cookieSecure,
            @Value("${app.security.cookies.same-site:Lax}") String cookieSameSite
    ) {
        this.usersService = usersService;
        this.refreshTokenService = refreshTokenService;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthSession session = usersService.login(request);

        return ResponseEntity.ok()
                .headers(authCookieHeaders(session))
                .body(new AuthResponse(session.fullName(), session.role()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshTokenCookie,
            @RequestBody(required = false) RefreshTokenRequest request
    ) {
        AuthSession session = refreshTokenService.refreshAccessToken(resolveRefreshToken(refreshTokenCookie, request));

        return ResponseEntity.ok()
                .headers(authCookieHeaders(session))
                .body(new AuthResponse(session.fullName(), session.role()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshTokenCookie,
            @RequestBody(required = false) RefreshTokenRequest request
    ) {
        String refreshToken = resolveOptionalRefreshToken(refreshTokenCookie, request);

        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        return ResponseEntity.noContent()
                .headers(clearCookieHeaders())
                .build();
    }

    private HttpHeaders authCookieHeaders(AuthSession session) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, authCookie(
                ACCESS_TOKEN_COOKIE,
                session.accessToken(),
                Duration.ofMinutes(accessTokenExpirationMinutes),
                "/"
        ).toString());
        headers.add(HttpHeaders.SET_COOKIE, authCookie(
                REFRESH_TOKEN_COOKIE,
                session.refreshToken(),
                Duration.ofDays(refreshTokenExpirationDays),
                "/api/auth"
        ).toString());
        return headers;
    }

    private HttpHeaders clearCookieHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, authCookie(ACCESS_TOKEN_COOKIE, "", Duration.ZERO, "/").toString());
        headers.add(HttpHeaders.SET_COOKIE, authCookie(REFRESH_TOKEN_COOKIE, "", Duration.ZERO, "/api/auth").toString());
        return headers;
    }

    private ResponseCookie authCookie(String name, String value, Duration maxAge, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(path)
                .maxAge(maxAge)
                .build();
    }

    private String resolveRefreshToken(String refreshTokenCookie, RefreshTokenRequest request) {
        String refreshToken = resolveOptionalRefreshToken(refreshTokenCookie, request);

        if (refreshToken == null) {
            throw new InvalidCredentialsException("Refresh token manquant");
        }

        return refreshToken;
    }

    private String resolveOptionalRefreshToken(String refreshTokenCookie, RefreshTokenRequest request) {
        if (refreshTokenCookie != null && !refreshTokenCookie.isBlank()) {
            return refreshTokenCookie;
        }

        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            return request.refreshToken();
        }

        return null;
    }
}
