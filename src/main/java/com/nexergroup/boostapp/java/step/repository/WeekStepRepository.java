package com.nexergroup.boostapp.java.step.repository;

import com.nexergroup.boostapp.java.step.model.WeekStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for interacting with the {@link WeekStep} entity in the database.
 * This class extends {@link JpaRepository}, and provides additional custom
 * methods for querying the month_step table in the database.
 * This class is annotated with {@link Repository} to mark it as a Spring Data repository.
 *
 */
@Repository
public interface WeekStepRepository extends JpaRepository<WeekStep, Long> {

    /**
     * Retrieve week-step entity with the given user id from the given year and week
     *
     * @param userId A user ID to search for
     * @param year   The year from which to retrieve the data
     * @param week   Actual week
     * @return An optional containing a {@link WeekStep} object with the given user id
     */
    Optional<WeekStep> findByUserIdAndYearAndWeek(String userId, int year, int week);

    List<WeekStep> findByUserIdAndYear(String userId, int year);

    /**
     * Retrieve the number of steps taken by user with given user id from the given year and week
     *
     *  @param userId A user ID to search for
     * @param year    The year from which to retrieve the data
     * @param week    The week from which to retrieve the data
     * @return        An optional containing the step count for the given user, year and week
     */
    @Query( "SELECT SUM(w.stepCount) " +
            "FROM WeekStep w " +
            "WHERE w.userId = :userId " +
            "AND w.year = :year " +
            "AND w.week = :week")
    Optional<Integer> getStepCountByUserIdYearAndWeek(@Param("userId") String userId, @Param("year") int year, @Param("week") int week);


    Optional<WeekStep> findTopByUserIdOrderByIdDesc(String userId);
}
