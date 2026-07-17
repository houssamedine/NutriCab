package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.dtos.UsersDto;
import com.bd.patientsmd.models.enums.UserRole;
import com.bd.patientsmd.models.requests.CreateUserRequest;
import com.bd.patientsmd.models.requests.LoginRequest;
import com.bd.patientsmd.models.requests.UpdateUserRequest;
import com.bd.patientsmd.models.responses.AuthSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsersService {

    UsersDto createUser(CreateUserRequest request);
    Page<UsersDto> getAllUsers(Pageable pageable);
    UsersDto getUserById(Long id);
    UsersDto updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    // Authentification
    AuthSession login(LoginRequest request);
    // Recherche / filtrage
    UsersDto getUserByEmail(String email);
    Page<UsersDto> getUsersByRole(UserRole role, Pageable pageable);
    Page<UsersDto> getUsersByActiveAndRole(boolean active, UserRole role, Pageable pageable);
    // Patients associés
    List<PatientDto> getUserPatients(Long userId);
    // Actions métier
    void changePassword(Long id, String oldPassword, String newPassword);
    UsersDto toggleUserActive(Long id);
}
