package se.sigma.boostapp.boost_app_java.repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;

@Repository
public interface StepRepository extends CrudRepository<Step, Long>{


	List<Step> findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
			String userId, LocalDateTime startTime, LocalDateTime endTime);


	@Query("SELECT new se.sigma.boostapp.boost_app_java.dto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) FROM Step s WHERE s.userId = :userId AND cast(s.startTime as date) >= :startDate AND cast(s.startTime as date) <= :endDate GROUP BY s.userId, cast(s.startTime as date) ORDER BY cast(s.startTime as date) ASC")
	List<StepDateDTO> getStepCount(@Param("userId") String userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT DISTINCT s.userId FROM Step s")
	List<String> getAllUsers();

	@Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId AND s.uploadedTime >= :startTime AND s.uploadedTime <= :endTime")
	Optional<Integer> getStepCountSum(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

	Optional<Step> findFirstByUserIdOrderByEndTimeDesc(String userId);



	//28.08.2020.
	//get earlier step, before last POST
	//@Query("SELECT sum(s.stepCount) FROM Step s WHERE s.userId = :userId")
	//@Query("SELECT s.stepCount FROM Step s WHERE s.userId = :userId")
	//int getOldStepSumPerUser(@Param("userId") String userId);






	//31.08.2020

	/*

	 @Query("UPDATE Company c SET c.address = :address WHERE c.id = :companyId")
    int updateAddress(@Param("companyId") int companyId, @Param("address") String address);
	@Modifying
@Query("update User u set u.firstname = ?1, u.lastname = ?2 where u.id = ?3")
void setUserInfoById(String firstname, String lastname, Integer userId);
	 */

	/*
	 @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<VehicleQueryDTO> updateVehicle(@PathVariable(value = "id") UUID id,
                                                         @RequestBody VehicleUpdateDTO vehicleUpdateDTO){
        return new ResponseEntity<>(vehicleCommandService.updateVehicle(id, vehicleUpdateDTO), HttpStatus.OK);
    }
	 */
	/*@Modifying
	@Query(value = "DELETE FROM Step s WHERE s.userId = :userId",nativeQuery = true)        // with native query
	public void deleteByUserId(@Param("userId") String userId);
	*/



	/*
	@Modifying(clearAutomatically = true)
  @Query(value = "Delete from UserOfferMapping c WHERE c.id=:id")
  public void deleteById(@Param("id") int id);
	 */


}


