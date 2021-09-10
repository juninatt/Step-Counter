package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface StepRepository extends JpaRepository<Step, Long> {

    /**
     * Delete step table on the Monday 00:00:01)
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Step")
    void deleteAllFromStep();


    /**
     * List of user using userId
     *
     * @param userId A user ID
     */
    Optional<List<Step>> findByUserId(@Param("userId") String userId);


    /**
     * Steps from step table using userId, start and end time
     *
     * @param userId    A user ID
     * @param startDate From start date
     * @param endDate   To end date
     */
    @Query("SELECT new se.sigma.boostapp.boost_app_java.dto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) FROM Step s WHERE s.userId = :userId AND cast(s.startTime as date) >= :startDate AND cast(s.startTime as date) <= :endDate GROUP BY s.userId, cast(s.startTime as date) ORDER BY cast(s.startTime as date) ASC")
    List<StepDateDTO> getStepCount(@Param("userId") String userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * All users from step table
     */
    @Query("SELECT DISTINCT s.userId FROM Step s")
    List<String> getAllUsers();

    /**
     * The sum of the steps from step table using userId, start,end and uploaded time
     *
     * @param userId    A user ID
     * @param startTime From start time
     * @param endTime   To end time
     */
    @Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId AND s.uploadedTime >= :startTime AND s.uploadedTime <= :endTime")
    Optional<Integer> getStepCountSum(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * User from step table using userId, year and week
     *
     * @param userId A user ID
     */
    Optional<Step> findFirstByUserIdOrderByEndTimeDesc(String userId);

}


