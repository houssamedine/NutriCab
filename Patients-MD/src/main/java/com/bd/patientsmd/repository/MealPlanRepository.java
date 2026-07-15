package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.MealPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan,Long> {
    Page<MealPlan> findByPatientId(Long patientId, Pageable pageable);
    Page<MealPlan> findByPatientUserId(Long userId, Pageable pageable);
    Page<MealPlan> findByObjectiveContainingIgnoreCase(String objective, Pageable pageable);
    Page<MealPlan> findByObjectiveContainingIgnoreCaseAndPatientUserId(String objective, Long userId, Pageable pageable);
    Page<MealPlan> findByCaloriesBetween(Integer minCalories, Integer maxCalories, Pageable pageable);
    Page<MealPlan> findByCaloriesBetweenAndPatientUserId(Integer minCalories, Integer maxCalories, Long userId, Pageable pageable);
    Optional<MealPlan> findFirstByPatientIdOrderByIdDesc(Long patientId);

    @Query("""
            select m from MealPlan m
            join m.patient p
            where lower(p.fullName) like lower(concat('%', :keyword, '%'))
               or lower(m.title) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(m.objective, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(m.content, '')) like lower(concat('%', :keyword, '%'))
               or (:calories is not null and m.calories = :calories)
            """)
    Page<MealPlan> search(
            @Param("keyword") String keyword,
            @Param("calories") Integer calories,
            Pageable pageable
    );

    @Query("""
            select m from MealPlan m
            join m.patient p
            where p.user.id = :userId
              and (
                   lower(p.fullName) like lower(concat('%', :keyword, '%'))
                or lower(m.title) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(m.objective, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(m.content, '')) like lower(concat('%', :keyword, '%'))
                or (:calories is not null and m.calories = :calories)
              )
            """)
    Page<MealPlan> searchByPatientUserId(
            @Param("keyword") String keyword,
            @Param("calories") Integer calories,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
