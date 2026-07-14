package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.InvalidCredentialsException;
import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.dtos.UsersDto;
import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.enums.UserRole;
import com.bd.patientsmd.models.mappers.PatientMapper;
import com.bd.patientsmd.models.mappers.UserMapper;
import com.bd.patientsmd.models.requests.CreateUserRequest;
import com.bd.patientsmd.models.requests.LoginRequest;
import com.bd.patientsmd.models.responses.AuthSession;
import com.bd.patientsmd.repository.PatientRepository;
import com.bd.patientsmd.repository.UsersRepository;
import com.bd.patientsmd.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsersServiceImpl implements UsersService{

    private final UsersRepository usersRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final boolean loginChecksEnabled;

    public UsersServiceImpl(
            UsersRepository usersRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            @Value("${app.security.auth.login-checks-enabled:true}") boolean loginChecksEnabled
    ) {
        this.usersRepository = usersRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.loginChecksEnabled = loginChecksEnabled;
    }

    @Override
    public UsersDto createUser(CreateUserRequest request) {
        if (usersRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email deja utilise");
        }

        Users user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.stampCreated();
        return UserMapper.toDto(usersRepository.saveAndFlush(user));
    }

    @Override
    public Page<UsersDto> getAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable)
                .map(UserMapper::toDto);
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
        user.setPassword(passwordEncoder.encode(request.password()));
        user.stampUpdated();
        return UserMapper.toDto(usersRepository.saveAndFlush(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!usersRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }
        usersRepository.deleteById(id);
    }

    private boolean passwordMatches(String rawPassword, Users user) {
        String savedPassword = user.getPassword();

        if (isBcryptHash(savedPassword) && passwordEncoder.matches(rawPassword, savedPassword)) {
            return true;
        }

        if (rawPassword.equals(savedPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            return true;
        }

        return false;
    }

    private boolean isBcryptHash(String password) {
        return password != null
                && (password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$"));
    }

    @Override
    public AuthSession login(LoginRequest request) {
        Users user = usersRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new InvalidCredentialsException("Email ou mot de passe incorrect"));

        if (loginChecksEnabled && !user.isActive()) {
            throw new IllegalArgumentException("Utilisateur désactive");
        }

        if (loginChecksEnabled && (user.getPassword() == null || user.getPassword().isBlank())) {
            throw new IllegalArgumentException("Mot de passe utilisateur non configure");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role utilisateur non configure");
        }

        if (loginChecksEnabled && !passwordMatches(request.password(), user)) {
            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }

        return new AuthSession(
                jwtService.generateToken(user),
                refreshTokenService.createRefreshToken(user),
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
    public Page<UsersDto> getUsersByRole(UserRole role, Pageable pageable) {
        return usersRepository.findByRole(role, pageable)
                .map(UserMapper::toDto);
    }

    @Override
    public Page<UsersDto> getUsersByActiveAndRole(boolean active, UserRole role, Pageable pageable) {
        Page<Users> users = role == null
                ? usersRepository.findByActive(active, pageable)
                : usersRepository.findByActiveAndRole(active, role, pageable);

        return users.map(UserMapper::toDto);
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

        if (!passwordMatches(oldPassword, user)) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public UsersDto toggleUserActive(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setActive(!user.isActive());
        user.stampUpdated();
        return UserMapper.toDto(usersRepository.saveAndFlush(user));
    }
    
}
