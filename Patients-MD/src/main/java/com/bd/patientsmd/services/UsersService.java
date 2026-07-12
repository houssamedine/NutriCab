package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.dtos.UsersDto;
import com.bd.patientsmd.models.enums.UserRole;
import com.bd.patientsmd.models.requests.CreateUserRequest;
import com.bd.patientsmd.models.requests.LoginRequest;
import com.bd.patientsmd.models.responses.AuthResponse;

import java.util.List;

public interface UsersService {

    UsersDto createUser(CreateUserRequest request);
    List<UsersDto> getAllUsers();
    UsersDto getUserById(Long id);
    UsersDto updateUser(Long id, CreateUserRequest request);
    void deleteUser(Long id);
    // Authentification
    AuthResponse login(LoginRequest request);
    // Recherche / filtrage
    UsersDto getUserByEmail(String email);
    List<UsersDto> getUsersByRole(UserRole role);
    List<UsersDto> getUsersByActiveAndRole(boolean active, UserRole role);
    // Patients associés
    List<PatientDto> getUserPatients(Long userId);
    // Actions métier
    void changePassword(Long id, String oldPassword, String newPassword);
    UsersDto toggleUserActive(Long id);
}
