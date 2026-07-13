package com.bd.patientsmd.models.entites;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        stampCreated();
    }

    @PreUpdate
    public void preUpdate() {
        stampUpdated();
    }

    public void stampCreated() {
        Instant now = Instant.now();
        String auditor = currentAuditor();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (createdBy == null) {
            createdBy = auditor;
        }

        if (updatedBy == null) {
            updatedBy = auditor;
        }
    }

    public void stampUpdated() {
        String auditor = currentAuditor();

        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (createdBy == null) {
            createdBy = auditor;
        }

        updatedAt = Instant.now();
        updatedBy = auditor;
    }

    public Instant getCreatedAt() {
        if (createdAt != null) {
            return createdAt;
        }

        if (updatedAt != null) {
            return updatedAt;
        }

        return Instant.now();
    }

    public Instant getUpdatedAt() {
        if (updatedAt != null) {
            return updatedAt;
        }

        if (createdAt != null) {
            return createdAt;
        }

        return Instant.now();
    }

    public String getCreatedBy() {
        if (createdBy != null && !createdBy.isBlank()) {
            return createdBy;
        }

        if (updatedBy != null && !updatedBy.isBlank()) {
            return updatedBy;
        }

        return "system";
    }

    public String getUpdatedBy() {
        if (updatedBy != null && !updatedBy.isBlank()) {
            return updatedBy;
        }

        if (createdBy != null && !createdBy.isBlank()) {
            return createdBy;
        }

        return "system";
    }

    private String currentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String subject = jwt.getSubject();
            return subject == null || subject.isBlank() ? "system" : subject;
        }

        String name = authentication.getName();
        return name == null || name.isBlank() ? "system" : name;
    }
}
