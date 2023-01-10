package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Step} entities in the database.
 * Extends {@link JpaRepository} to provide basic CRUD operations and additional
 * methods for querying the database for specific information about steps.
 * This class is annotated with {@link Repository} to mark it as a Spring Data repository.
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
    @Query("DELETE FROM Step")
    void deleteAllFromStep();


    /**
     * Retrieves a list of steps for a user with the given user ID.
     * @param userId A user ID to search for
     * @return An Optional containing a list of steps for the given user, or an empty Optional if no steps were found
     */
    Optional<List<Step>> findByUserId(@Param("userId") String userId);


    /**
     * Retrieves steps for a user with the given user ID, start- and end date.
     *
     @param userId A user ID to search for
     @param startDate From start date
     @param endDate To end date
     @return A list of {@link StepDateDTO} objects containing the date and step count for the specified user, date range
     */
    @Query("SELECT new se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) FROM Step s WHERE s.userId = :userId AND cast(s.startTime as date) >= :startDate AND cast(s.startTime as date) <= :endDate GROUP BY s.userId, cast(s.startTime as date) ORDER BY cast(s.startTime as date) ASC")
    List<StepDateDTO> getStepCount(@Param("userId") String userId, @Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    /**
     * Retrieves all distinct users from step table
     * @return A list of user id:s from the step table
     */
    @Query("SELECT DISTINCT s.userId FROM Step s")
    List<String> getAllUsers();

    /**
     * The sum of the steps from step table using userId, start,end and uploaded time
     *
     @param userId A user ID to search for
     * @param startTime From start time
     * @param endTime   To end time
     * @return An optional containing sum of the steps from step table using userId, start,end and uploaded time.
     */
    @Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId AND s.uploadedTime >= :startTime AND s.uploadedTime <= :endTime")
    Optional<Integer> getStepCountSum(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * retrieves the latest registered step entity for a user with the given user id.
     *
     @param userId A user ID to search for
     @return An optional containing a {@link Step} from the step-table using userId.
     */
    Optional<Step> findFirstByUserIdOrderByEndTimeDesc(String userId);

}


