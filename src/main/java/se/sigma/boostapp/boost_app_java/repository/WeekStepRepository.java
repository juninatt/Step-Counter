package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import se.sigma.boostapp.boost_app_java.model.WeekStep;

import java.util.Optional;


@Repository
public interface WeekStepRepository extends CrudRepository<WeekStep,Long> {

    Optional<WeekStep> findByUserIdAndYear(String userId, int year);

}
