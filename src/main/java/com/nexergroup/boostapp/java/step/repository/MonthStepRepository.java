package com.nexergroup.boostapp.java.step.repository;

import com.nexergroup.boostapp.java.step.model.MonthStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for interacting with the {@link MonthStep} entity in the database.
 * This class extends {@link JpaRepository}, and provides additional custom
 * methods for querying the month_step table in the database.
 * This class is annotated with {@link Repository} to mark it as a Spring Data repository.
 *
 * @see QueryHelper
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
    @Query(QueryHelper.SELECT_STEP_COUNT_YEAR_MONTH)
    Optional<Integer> getStepCountByUserIdYearAndMonth(@Param("userId") String userId, @Param("year") int year, @Param("month") int month);

    /**
     * Increments the stepCount field of a specific {@link MonthStep} object in the database.
     *
     * @param id The id of the {@link MonthStep} object to be updated.
     * @param increment The number to be added to the existing stepCount.
     */
    @Transactional
    @Modifying
    @Query("UPDATE MonthStep ms SET ms.stepCount = :increment WHERE ms.id = :id")
    void setTotalStepCountById(@Param("id") Long id, @Param("increment") int increment);

    Optional<MonthStep> findTopByUserIdOrderByIdDesc(String userId);
}




