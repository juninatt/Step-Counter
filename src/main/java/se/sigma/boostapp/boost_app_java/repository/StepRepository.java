package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

/**
 * 
 * @author SigmaIT
 *
 */
@Repository
public interface StepRepository extends CrudRepository<Step, Long>{
	
	/**
	 * @author SigmaIT
	 * Delete step table on the Monday 00:00:01)
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM Step")
	void deleteAllFromStep();
	
	
	/**
	 * @author SigmaIT
	 * @param userId
	 * @return List of user using userId
	 */
	Optional<List<Step>> findByUserId(@Param("userId")String userId);


	/**
	 * @author SigmaIT
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return Steps from step table using userId, start and end time
	 */
	@Query("SELECT new se.sigma.boostapp.boost_app_java.dto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) FROM Step s WHERE s.userId = :userId AND cast(s.startTime as date) >= :startDate AND cast(s.startTime as date) <= :endDate GROUP BY s.userId, cast(s.startTime as date) ORDER BY cast(s.startTime as date) ASC")
	List<StepDateDTO> getStepCount(@Param("userId") String userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	/**
	 * @author SigmaIT
	 * @return All users from step table
	 */
	@Query("SELECT DISTINCT s.userId FROM Step s")
	List<String> getAllUsers();

	/**
	 * @author SigmaIT
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return The sum of the steps from step table using userId, start,end and uploaded time
	 */
	@Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId AND s.uploadedTime >= :startTime AND s.uploadedTime <= :endTime")
	Optional<Integer> getStepCountSum(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

	/**
	 * @author SigmaIT
	 * @param userId
	 * @return 
	 */
	Optional<Step> findFirstByUserIdOrderByEndTimeDesc(String userId);

}


