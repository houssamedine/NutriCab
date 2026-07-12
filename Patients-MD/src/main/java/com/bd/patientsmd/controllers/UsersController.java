package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.dtos.UsersDto;
import com.bd.patientsmd.models.enums.UserRole;
import com.bd.patientsmd.models.requests.ChangePasswordRequest;
import com.bd.patientsmd.models.requests.CreateUserRequest;
import com.bd.patientsmd.models.requests.LoginRequest;
import com.bd.patientsmd.models.responses.AuthResponse;
import com.bd.patientsmd.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public UsersDto createUser(@Valid @RequestBody CreateUserRequest request) {
        return usersService.createUser(request);
    }

    @GetMapping
    public List<UsersDto> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UsersDto getUserById(@PathVariable Long id) {
        return usersService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UsersDto updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        return usersService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return usersService.login(request);
    }

    @GetMapping("/email/{email}")
    public UsersDto getUserByEmail(@PathVariable String email) {
        return usersService.getUserByEmail(email);
    }

    @GetMapping("/role/{role}")
    public List<UsersDto> getUsersByRole(@PathVariable UserRole role) {
        return usersService.getUsersByRole(role);
    }

    @GetMapping("/filter")
    public List<UsersDto> getUsersByActiveAndRole(@RequestParam boolean active, @RequestParam(required = false) UserRole role) {
        return usersService.getUsersByActiveAndRole(active, role);
    }

    @GetMapping("/{userId}/patients")
    public List<PatientDto> getUserPatients(@PathVariable Long userId) {
        return usersService.getUserPatients(userId);
    }

    @PatchMapping("/{id}/password")
    public void changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        usersService.changePassword(id, request.oldPassword(), request.newPassword());
    }

    @PatchMapping("/{id}/status")
    public UsersDto toggleUserActive(@PathVariable Long id) {
        return usersService.toggleUserActive(id);
    }
}
