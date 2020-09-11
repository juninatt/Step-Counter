package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import se.sigma.boostapp.boost_app_java.model.WeekStep;


@Repository
public interface WeekStepRepository extends CrudRepository<WeekStep,Integer> {

}
