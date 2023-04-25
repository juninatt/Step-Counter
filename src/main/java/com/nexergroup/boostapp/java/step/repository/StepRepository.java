package com.nexergroup.boostapp.java.step.repository;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
import com.nexergroup.boostapp.java.step.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Step} entities in the database.
 * Extends {@link JpaRepository} to provide basic CRUD operations and additional
 * methods for querying the database for specific information about steps.
 * This class is annotated with {@link Repository} to mark it as a Spring Data repository.
 *
 * @see QueryHelper
 */
@Repository
public interface StepRepository extends JpaRepository<Step, Long> {

    /**
     * Deletes all data from the step table.
     * This method is annotated with {@link Transactional} to mark it as a transactional operation,
     * and {@link Modifying} to indicate that it modifies the database.
     */
    @Transactional
    @Modifying
    @Query(QueryHelper.DELETE_ALL_STEP)
    void deleteAllFromStep();

    /**
     * Update the stepCount-, endTime- and uploadTime- fields of a {@link Step} object in the database.
     *
     * @param step The {@link Step} object to update.
     * @param stepCount The new stepCount of the step object
     * @param endTime The new endTime value of the object.
     * @param uploadTime The new uploadTime value of the object.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Step s SET s.stepCount = :stepCount, s.endTime = :endTime, s.uploadTime = :uploadTime WHERE s = :step")
    void incrementStepCountAndUpdateTimes(
            @Param("step") Step step,
            @Param("stepCount") int stepCount,
            @Param("endTime") ZonedDateTime endTime,
            @Param("uploadTime") ZonedDateTime uploadTime);


    /**
     * Retrieves a list of steps for a user with the given user ID.
     * @param userId A user ID to search for
     * @return An Optional containing a list of steps for the given user, or an empty Optional if no steps were found
     */
    Optional<List<Step>> getListOfStepsByUserId(@Param("userId") String userId);

    /**
     * Retrieves steps for a user with the given user ID, start- and end date.
     *
     @param userId A user ID to search for
     @param startDate From start date
     @param endDate To end date
     @return A list of {@link StepDateDTO} objects containing the date and step count for the specified user, date range
     */
    @Query(QueryHelper.SELECT_STEP_DATA_WITHIN_TIME_RANGE)
    List<StepDateDTO> getStepDataByUserIdAndDateRange(@Param("userId") String userId, @Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    /**
     * Retrieves all distinct users from step table
     * @return A list of user id:s from the step table
     */
    @Query(QueryHelper.SELECT_ALL_USER_ID)
    List<String> getListOfAllDistinctUserId();

    /**
     * The sum of the steps from step table using userId, start,end and uploaded time
     *
     @param userId A user ID to search for
      * @param startTime From start time
     * @param endTime   To end time
     * @return An optional containing sum of the steps from step table using userId, start,end and uploaded time.
     */
    @Query(QueryHelper.SELECT_STEP_COUNT_WITHIN_TIME_RANGE)
    Optional<Integer> getStepCountByUserIdAndDateRange(@Param("userId") String userId, @Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

    /**
     * retrieves the latest registered step entity for a user with the given user id.
     *
     @param userId A user ID to search for
     @return An optional containing a {@link Step} from the step-table using userId.
     */
    Optional<Step> findFirstByUserIdOrderByEndTimeDesc(String userId);
}


