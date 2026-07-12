package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.dtos.UsersDto;
import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.enums.UserRole;
import com.bd.patientsmd.models.mappers.PatientMapper;
import com.bd.patientsmd.models.mappers.UserMapper;
import com.bd.patientsmd.models.requests.CreateUserRequest;
import com.bd.patientsmd.models.requests.LoginRequest;
import com.bd.patientsmd.models.responses.AuthResponse;
import com.bd.patientsmd.repository.PatientRepository;
import com.bd.patientsmd.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UsersServiceImpl implements UsersService{

    private final UsersRepository usersRepository;
    private final PatientRepository patientRepository;

    @Override
    public UsersDto createUser(CreateUserRequest request) {
        if (usersRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email deja utilise");
        }

        Users user = UserMapper.toEntity(request);
        return UserMapper.toDto(usersRepository.save(user));
    }

    @Override
    public List<UsersDto> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UsersDto getUserById(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return UserMapper.toDto(user);
    }

    @Override
    public UsersDto updateUser(Long id, CreateUserRequest request) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        usersRepository.findByEmail(request.email())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("Email deja utilise");
                });

        UserMapper.updateEntity(user, request);
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!usersRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }
        usersRepository.deleteById(id);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Users user = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Utilisateur désactive");
        }

        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        return new AuthResponse(
                UUID.randomUUID().toString(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    @Override
    public UsersDto getUserByEmail(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UsersDto> getUsersByRole(UserRole role) {
        return usersRepository.findByRole(role)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public List<UsersDto> getUsersByActiveAndRole(boolean active, UserRole role) {
        List<Users> users = role == null
                ? usersRepository.findByActive(active)
                : usersRepository.findByActiveAndRole(active, role);

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public List<PatientDto> getUserPatients(Long userId) {
        if (!usersRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }

        return patientRepository.findByUserId(userId)
                .stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        user.setPassword(newPassword);
    }

    @Override
    public UsersDto toggleUserActive(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setActive(!user.isActive());
        return UserMapper.toDto(user);
    }
}
