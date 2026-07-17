package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.dtos.UsersDto;
import com.bd.patientsmd.models.enums.UserRole;
import com.bd.patientsmd.models.requests.ChangePasswordRequest;
import com.bd.patientsmd.models.requests.CreateUserRequest;
import com.bd.patientsmd.models.requests.UpdateUserRequest;
import com.bd.patientsmd.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public UsersDto createUser(@Valid @RequestBody CreateUserRequest request) {
        return usersService.createUser(request);
    }

    @GetMapping
    public Page<UsersDto> getAllUsers(Pageable pageable) {
        return usersService.getAllUsers(pageable);
    }

    @GetMapping("/{id}")
    public UsersDto getUserById(@PathVariable Long id) {
        return usersService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UsersDto updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return usersService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
    }

    @GetMapping("/email/{email}")
    public UsersDto getUserByEmail(@PathVariable String email) {
        return usersService.getUserByEmail(email);
    }

    @GetMapping("/role/{role}")
    public Page<UsersDto> getUsersByRole(@PathVariable UserRole role, Pageable pageable) {
        return usersService.getUsersByRole(role, pageable);
    }

    @GetMapping("/filter")
    public Page<UsersDto> getUsersByActiveAndRole(
            @RequestParam boolean active,
            @RequestParam(required = false) UserRole role,
            Pageable pageable
    ) {
        return usersService.getUsersByActiveAndRole(active, role, pageable);
    }

    @GetMapping("/{userId}/patients")
    public List<PatientDto> getUserPatients(@PathVariable Long userId) {
        return usersService.getUserPatients(userId);
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        usersService.changePassword(id, request.oldPassword(), request.newPassword());
    }

    @PatchMapping("/{id}/status")
    public UsersDto toggleUserActive(@PathVariable Long id) {
        return usersService.toggleUserActive(id);
    }
}
