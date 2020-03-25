package se.sigma.boostapp.boost_app_java.repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;

public interface StepRepository extends CrudRepository<Step, Long>{

	List<Step> findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
			String userId, LocalDateTime startTime, LocalDateTime endTime);

	@Query("SELECT new se.sigma.boostapp.boost_app_java.dto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) FROM Step s WHERE s.userId = :userId AND cast(s.startTime as date) >= :startDate AND cast(s.startTime as date) <= :endDate GROUP BY s.userId, cast(s.startTime as date) ORDER BY cast(s.startTime as date) ASC")
	List<StepDateDTO> getStepCount(@Param("userId") String userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT DISTINCT s.userId FROM Step s")
	List<String> getAllUsers();

	@Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId AND s.uploadedTime >= :startTime AND s.uploadedTime <= :endTime")
	Optional<Integer> getStepCountSum(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

	Iterable<Step> findByUserId(String userId);

	Optional<Step> findFirstByUserIdOrderByEndTimeDesc(String userId);
}


