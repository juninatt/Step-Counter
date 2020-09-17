package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StepRepository extends CrudRepository<Step, Long>{


	List<Step> findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
			String userId, LocalDateTime startTime, LocalDateTime endTime);


	@Query("SELECT new se.sigma.boostapp.boost_app_java.dto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) FROM Step s WHERE s.userId = :userId AND cast(s.startTime as date) >= :startDate AND cast(s.startTime as date) <= :endDate GROUP BY s.userId, cast(s.startTime as date) ORDER BY cast(s.startTime as date) ASC")
	List<StepDateDTO> getStepCount(@Param("userId") String userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT DISTINCT s.userId FROM Step s")
	List<String> getAllUsers();

		//kanske kan använda för räkningar av steg per vecka/månad- bara använda nya tabell i databas????
	@Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId AND s.uploadedTime >= :startTime AND s.uploadedTime <= :endTime")
	Optional<Integer> getStepCountSum(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

	Optional<Step> findFirstByUserIdOrderByEndTimeDesc(String userId);

	void deleteAll();


	//28.08.2020.
	//get earlier step, before last POST
	//@Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId")

	//int getOldStepSumPerUser(@Param("userId") String userId);

	//31.08.2020
	/*@Modifying
	@Query(value = "DELETE FROM Step s WHERE s.userId = :userId",nativeQuery = true)        // with native query
	public void deleteByUserId(@Param("userId") String userId);
	*/

	/*
	@Modifying(clearAutomatically = true)
  @Query(value = "Delete from UserOfferMapping c WHERE c.id=:id")
  public void deleteById(@Param("id") int id);
	 */

	/* @Transactional
	@Modifying
	void deleteById(String id);*/

}


