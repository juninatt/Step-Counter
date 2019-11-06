package se.sigma.boostapp.boost_app_java.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import se.sigma.boostapp.boost_app_java.model.Step;

public interface StepRepository extends CrudRepository<Step, Long>{

	List<Step> findByUserId(int userId);
	List<Step> findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
			int userId, LocalDateTime startTime, LocalDateTime endTime);
}
