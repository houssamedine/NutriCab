package com.bd.patientsmd.models.mappers;

import com.bd.patientsmd.models.dtos.UsersDto;
import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.requests.CreateUserRequest;
import com.bd.patientsmd.models.requests.UpdateUserRequest;
import com.bd.patientsmd.models.responses.UserResponse;
import com.bd.patientsmd.models.responses.UserSummaryResponse;

import java.util.List;

public class UserMapper {

    public static UsersDto toDto(Users user) {
        if (user == null) return null;

        return new UsersDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getPatients() == null
                        ? List.of()
                        : user.getPatients().stream()
                        .map(PatientMapper::toDto)
                        .toList(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getCreatedBy(),
                user.getUpdatedBy()
        );
    }

    public static UserSummaryResponse toSummaryResponse(Users user) {
        if (user == null) return null;

        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public static UserResponse toResponse(Users user) {
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getPatients() == null
                        ? List.of()
                        : user.getPatients().stream()
                        .map(PatientMapper::toSummaryResponse)
                        .toList()
        );
    }

    public static Users toEntity(CreateUserRequest request) {
        if (request == null) return null;

        return Users.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(request.password())
                .role(request.role())
                .build();
    }

    public static void updateEntity(Users user, CreateUserRequest request) {
        if (user == null || request == null) return;

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(request.role());
    }

    public static void updateEntity(Users user, UpdateUserRequest request) {
        if (user == null || request == null) return;

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setRole(request.role());
    }
}
