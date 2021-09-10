package se.sigma.boostapp.boost_app_java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

import java.util.Optional;


@Repository
public interface WeekStepRepository extends JpaRepository<WeekStep, Long> {


    /**
     * User from weekstep table using userId, year and week
     *
     * @param userId A user ID
     * @param year   Actual year
     * @param week   Actual week
     */
    Optional<WeekStep> findByUserIdAndYearAndWeek(String userId, int year, int week);

    /**
     * Steps from weekstep table using userId, year and week
     *
     * @param userId
     * @param year
     * @param week
     * @return
     */
    @Query("SELECT w.steps FROM WeekStep w WHERE w.userId = :userId AND w.year = :year AND w.week = :week")
    Optional<Integer> getStepCountWeek(@Param("userId") String userId, @Param("year") int year, @Param("week") int week);

}
