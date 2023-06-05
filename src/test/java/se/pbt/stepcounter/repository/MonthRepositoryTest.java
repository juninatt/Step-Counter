package se.pbt.stepcounter.repository;

import se.pbt.stepcounter.model.MonthStep;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MonthRepositoryTest {

    @Autowired
    MonthStepRepository monthStepRepository;

    @Before
    public void setUp() {
        MonthStep m1 = new MonthStep("daniel", 12, 2020, 300);
        monthStepRepository.save(m1);
    }

    @Test
    public void getStepCountMonth_test() {
       var test =  monthStepRepository.getStepCountByUserIdYearAndMonth("daniel", 2020, 12);
        assertEquals(300, (int) test.get());
    }
}
