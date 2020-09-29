package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se.sigma.boostapp.boost_app_java.model.MonthStep;
import java.util.Optional;

/**
 * 
 * @author SigmaIT
 *
 */
@Repository
public interface MonthStepRepository extends CrudRepository<MonthStep,Long>{

   /**
    * @author SigmaIT
    * @param userId
    * @param year
    * @param month
    * @return User i monthstep table using userId, year and month
    */
	Optional<MonthStep> findByUserIdAndYearAndMonth(String userId, int year, int month);

  

   /**
    * @author SigmaIT
    * @param userId
    * @param year
    * @param month
    * @return Steps from monthstep table using userId,year and month
    */
	@Query("SELECT m.steps FROM MonthStep m WHERE m.userId = :userId AND m.year = :year AND m.month = :month")
    Optional<Integer> getStepCountMonth(@Param("userId")String userId,@Param("year") int year,@Param("month") int month);


}




