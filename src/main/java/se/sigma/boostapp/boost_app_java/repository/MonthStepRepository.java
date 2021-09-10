package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se.sigma.boostapp.boost_app_java.model.MonthStep;
import java.util.Optional;


@Repository
public interface MonthStepRepository extends JpaRepository<MonthStep,Long> {

   /**
    * User i monthstep table using userId, year and month
    * @param userId A user ID
    * @param year Actual year
    * @param month Actual month
    */
	Optional<MonthStep> findByUserIdAndYearAndMonth(String userId, int year, int month);

  

   /**
    * Steps from monthstep table using userId,year and month
    * @param userId A user ID
    * @param year Actual year
    * @param month Actual month
    */
	@Query("SELECT m.steps FROM MonthStep m WHERE m.userId = :userId AND m.year = :year AND m.month = :month")
    Optional<Integer> getStepCountMonth(@Param("userId")String userId,@Param("year") int year,@Param("month") int month);
}




