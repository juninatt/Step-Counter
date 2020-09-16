package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se.sigma.boostapp.boost_app_java.model.MonthStep;

import java.util.Optional;

@Repository
public interface MonthStepRepository extends CrudRepository<MonthStep,Long>{

   // @Query("SELECT MonthStep FROM MonthStep m WHERE m.userId = :userId AND m.year = :year ORDER BY m.year DESC ")
   // Optional<MonthStep> findFirstByUserIdAndYear(@Param("userId")String userId, @Param("year")int year);

    Optional<MonthStep> findFirstByUserId(String userId);
}




