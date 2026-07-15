package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Appointments;
import com.bd.patientsmd.models.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments, Long> {
    Page<Appointments> findByPatientId(Long patientId, Pageable pageable);
    Page<Appointments> findByPatientUserId(Long userId, Pageable pageable);
    Page<Appointments> findByStatus(AppointmentStatus status, Pageable pageable);
    Page<Appointments> findByStatusAndPatientUserId(AppointmentStatus status, Long userId, Pageable pageable);

    @Query("""
            select a from Appointments a
            join a.patient p
            where lower(p.fullName) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(a.notes, '')) like lower(concat('%', :keyword, '%'))
               or (:status is not null and a.status = :status)
            """)
    Page<Appointments> search(
            @Param("keyword") String keyword,
            @Param("status") AppointmentStatus status,
            Pageable pageable
    );

    @Query("""
            select a from Appointments a
            join a.patient p
            where p.user.id = :userId
              and (
                   lower(p.fullName) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(a.notes, '')) like lower(concat('%', :keyword, '%'))
                or (:status is not null and a.status = :status)
              )
            """)
    Page<Appointments> searchByPatientUserId(
            @Param("keyword") String keyword,
            @Param("status") AppointmentStatus status,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
