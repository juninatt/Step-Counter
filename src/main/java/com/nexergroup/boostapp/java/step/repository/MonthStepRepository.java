package com.nexergroup.boostapp.java.step.repository;

import com.nexergroup.boostapp.java.step.model.MonthStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for interacting with the {@link MonthStep} entity in the database.
 * This class extends {@link JpaRepository}, and provides additional custom
 * methods for querying the month_step table in the database.
 * This class is annotated with {@link Repository} to mark it as a Spring Data repository.
 *
 */
@Repository
public interface MonthStepRepository extends JpaRepository<MonthStep, Long> {

    /**
     * Find user by user id, year and month
     *
     * @param userId A user ID
     * @param year   Actual year
     * @param month  Actual month
     * @return Optional of MonthStep
     */
    Optional<MonthStep> findByUserIdAndYearAndMonth(String userId, int year, int month);


    /**
     * Steps from month-step table using userId ,year and month
     *
     * @param userId A user ID
     * @param year   Actual year
     * @param month  Actual month
     * @return Optional of Integer
     */
    @Query("SELECT SUM(m.stepCount) " +
            "FROM MonthStep m " +
            "WHERE m.userId = :userId " +
            "AND m.year = :year " +
            "AND m.month = :month")
    Optional<Integer> getStepCountByUserIdYearAndMonth(@Param("userId") String userId, @Param("year") int year, @Param("month") int month);


    Optional<MonthStep> findTopByUserIdOrderByIdDesc(String userId);

    List<MonthStep> findByUserIdAndYear(String userId, int year);
}




