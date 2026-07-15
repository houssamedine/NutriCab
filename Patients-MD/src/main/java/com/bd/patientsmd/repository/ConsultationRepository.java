package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Consultations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultations, Long> {
    Page<Consultations> findByPatientId(Long patientId, Pageable pageable);
    Page<Consultations> findByPatientUserId(Long userId, Pageable pageable);
    List<Consultations> findByPatientIdOrderByConsultationDateAsc(Long patientId);
    List<Consultations> findTop10ByOrderByConsultationDateDesc();
    List<Consultations> findTop10ByPatientUserIdOrderByConsultationDateDesc(Long userId);

    @Query("""
            select a from Consultations a
            join a.patient p
            where lower(p.fullName) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(a.notes, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(a.recommendations, '')) like lower(concat('%', :keyword, '%'))
               or (:consultationDate is not null and a.consultationDate = :consultationDate)
               or (:numericValue is not null and (a.weightKg = :numericValue or a.waistCm = :numericValue))
            """)
    Page<Consultations> search(
            @Param("keyword") String keyword,
            @Param("consultationDate") LocalDate consultationDate,
            @Param("numericValue") Double numericValue,
            Pageable pageable
    );

    @Query("""
            select a from Consultations a
            join a.patient p
            where p.user.id = :userId
              and (
                   lower(p.fullName) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(a.notes, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(a.recommendations, '')) like lower(concat('%', :keyword, '%'))
                or (:consultationDate is not null and a.consultationDate = :consultationDate)
                or (:numericValue is not null and (a.weightKg = :numericValue or a.waistCm = :numericValue))
              )
            """)
    Page<Consultations> searchByPatientUserId(
            @Param("keyword") String keyword,
            @Param("consultationDate") LocalDate consultationDate,
            @Param("numericValue") Double numericValue,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
