package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se.sigma.boostapp.boost_app_java.model.MonthStep;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface MonthStepRepository extends CrudRepository<MonthStep,Long>{

   // @Query("SELECT MonthStep FROM MonthStep m WHERE m.userId = :userId AND m.year = :year ORDER BY m.year DESC ")
   // Optional<MonthStep> findFirstByUserIdAndYear(@Param("userId")String userId, @Param("year")int year);

    Optional<MonthStep> findFirstByUserIdAndYear(String userId, int year);

 /* //  insert into testmonth (user_id, week2) values ('danijela', 900)

    //@Query(value = "insert into Logger (redirect,user_id) VALUES (:insertLink,:id)", nativeQuery = true)
    @Modifying
    @Query(value = "insert into stepmonth  (user_id, :column, year) values (:userId, :stepCount, :year)", nativeQuery = true)
    @Transactional
    void updateColumnWithNewUser(@Param("userId")String userId, @Param("stepCount")int stepCount, @Param("year")int year, @Param("column")String column);*/

}




