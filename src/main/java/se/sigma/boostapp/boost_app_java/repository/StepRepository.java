package se.sigma.boostapp.boost_app_java.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import se.sigma.boostapp.boost_app_java.model.Step;

public interface StepRepository extends CrudRepository<Step, Long>{

	List<Step> findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
			String userId, LocalDateTime startTime, LocalDateTime endTime);

	@Query("SELECT to_char(s.startTime, 'yyyy-mm-dd'), sum(s.stepCount) FROM Step s WHERE s.userId = :userId AND to_char(s.startTime, 'yyyy-mm-dd') >= :startDate AND to_char(s.startTime, 'yyyy-mm-dd') <= :endDate GROUP BY s.userId, to_char(s.startTime, 'yyyy-mm-dd') ORDER BY to_char(s.startTime,'yyyy-mm-dd') ASC")
	List getStepCount(@Param("userId") String userId, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
