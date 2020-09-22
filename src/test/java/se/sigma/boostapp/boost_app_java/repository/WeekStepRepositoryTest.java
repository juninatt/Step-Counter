package se.sigma.boostapp.boost_app_java.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WeekStepRepositoryTest {

    @Autowired
    private WeekStepRepository weekStepRepository;

    @Before
    public void setUp() {

        WeekStep wStep1 = new WeekStep("johanna", 1, 2020, 300);
        WeekStep wStep2 = new WeekStep("danijela", 1, 2020, 400);
        WeekStep wStep3 = new WeekStep("yahya", 1, 2020, 500);
        WeekStep wStep4 = new WeekStep("gabrielle", 1,2020, 900);

        weekStepRepository.save(wStep1);
        weekStepRepository.save(wStep2);
        weekStepRepository.save(wStep3);
        weekStepRepository.save(wStep4);
    }

    @Test
    public void shouldReturnWeekStepForWeek1Year2020() {

        var test = weekStepRepository.findByUserIdAndYearAndWeek("johanna", 2020, 1);
        assertEquals(300, test);

    }

}