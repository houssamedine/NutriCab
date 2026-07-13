package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Users> findByRole(UserRole role, Pageable pageable);
    Page<Users> findByActive(boolean active, Pageable pageable);
    Page<Users> findByActiveAndRole(boolean active, UserRole role, Pageable pageable);
}
