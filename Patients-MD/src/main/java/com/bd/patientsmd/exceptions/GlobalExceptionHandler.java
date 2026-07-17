package com.bd.patientsmd.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;



@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("API error 404: {}", ex.getMessage());
        return new ErrorResponse(
                LocalDateTime.now(),
                404,
                "NOT_FOUND",
                request.getRequestURI(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("API error 401: {}", ex.getMessage());
        return new ErrorResponse(
                LocalDateTime.now(),
                401,
                "UNAUTHORIZED",
                request.getRequestURI(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("API error 409: {}", ex.getMessage());
        return new ErrorResponse(
                LocalDateTime.now(),
                409,
                "CONFLICT",
                request.getRequestURI(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("API validation error 400: {}", message);
        return new ErrorResponse(
                LocalDateTime.now(),
                400,
                "VALIDATION_ERROR",
                request.getRequestURI(),
                message
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnreadableJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("API JSON error 400: {}", ex.getMessage());
        return new ErrorResponse(
                LocalDateTime.now(),
                400,
                "INVALID_JSON",
                request.getRequestURI(),
                "Le body JSON est invalide ou ne correspond pas au format attendu"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("API error 403: {}", ex.getMessage());
        return new ErrorResponse(
                LocalDateTime.now(),
                403,
                "FORBIDDEN",
                request.getRequestURI(),
                "Vous n'avez pas les droits necessaires pour acceder a cette ressource"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("API error 400: {}", ex.getMessage());
        return new ErrorResponse(
                LocalDateTime.now(),
                400,
                "BAD_REQUEST",
                request.getRequestURI(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(Exception ex, HttpServletRequest request) {
        log.error("API error 500", ex);
        return new ErrorResponse(
                LocalDateTime.now(),
                500,
                "INTERNAL_SERVER_ERROR",
                request.getRequestURI(),
                "Erreur interne du serveur"
        );
    }
}
