package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Users> findByRole(UserRole role);
    List<Users> findByActive(boolean active);
    List<Users> findByActiveAndRole(boolean active, UserRole role);
}
