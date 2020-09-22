package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import se.sigma.boostapp.boost_app_java.model.MonthStep;
import java.util.Optional;

@Repository
public interface MonthStepRepository extends CrudRepository<MonthStep,Long>{

    Optional<MonthStep> findByUserIdAndYearAndMonth(String userId, int year, int month);

    Optional<MonthStep> findFirstByUserIdAndYear(String userId, int year);


}




