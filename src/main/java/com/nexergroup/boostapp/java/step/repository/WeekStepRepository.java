package com.nexergroup.boostapp.java.step.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.nexergroup.boostapp.java.step.model.WeekStep;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Repository for interacting with the {@link WeekStep} entity in the database.
 * This class extends {@link JpaRepository}, and provides additional custom
 * methods for querying the month_step table in the database.
 * This class is annotated with {@link Repository} to mark it as a Spring Data repository.
 *
 * @see QueryHelper
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

    /**
     * Retrieve the number of steps taken by user with given user id from the given year and week
     *
     *  @param userId A user ID to search for
     * @param year    The year from which to retrieve the data
     * @param week    The week from which to retrieve the data
     * @return        An optional containing the step count for the given user, year and week
     */
    @Query(QueryHelper.SELECT_STEP_COUNT_YEAR_AND_WEEK)
    Optional<Integer> getStepCountByUserIdYearAndWeek(@Param("userId") String userId, @Param("year") int year, @Param("week") int week);


    /**
     * Increments the stepCount field of a specific {@link WeekStep} object in the database.
     *
     * @param id The id of the {@link WeekStep} object to be updated.
     * @param increment The number to be added to the existing stepCount.
     */
    @Transactional
    @Modifying
    @Query("UPDATE WeekStep ws SET ws.stepCount = ws.stepCount + :increment WHERE ws.id = :id")
    void incrementWeekStepCount(@Param("id") Long id, @Param("increment") int increment);


}
