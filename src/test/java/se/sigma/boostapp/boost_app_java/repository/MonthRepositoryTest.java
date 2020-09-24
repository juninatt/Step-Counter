package se.sigma.boostapp.boost_app_java.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.sigma.boostapp.boost_app_java.model.MonthStep;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MonthRepositoryTest {

    @Autowired
    MonthStepRepository monthStepRepository;

    @Before
    public void setUp() throws Exception {
        MonthStep m1 = new MonthStep("daniel", 12, 2020, 300);
        monthStepRepository.save(m1);
    }

    @Test
    public void getStepCountMonth_test() {
       var test =  monthStepRepository.getStepCountMonth("daniel", 2020, 12);
        assertTrue(300 == test.get());
    }
}
