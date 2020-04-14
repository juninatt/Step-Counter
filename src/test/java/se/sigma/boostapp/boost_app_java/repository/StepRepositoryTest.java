package se.sigma.boostapp.boost_app_java.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.sigma.boostapp.boost_app_java.model.Step;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StepRepositoryTest {

    @Autowired
    private StepRepository stepRepository;

    @Before
    public void setUp() {
        Step step = new Step("4f6648d2-1434-40a5-aa66-7bb07d022841", 400,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));
        stepRepository.save(step);
    }

    @Test
    public void findLatestStepById_test() {
        Step step = stepRepository.findFirstByUserIdOrderByEndTimeDesc("4f6648d2-1434-40a5-aa66-7bb07d022841").get();

        assertThat(step.getStepCount()).isEqualTo(400);
    }
}
