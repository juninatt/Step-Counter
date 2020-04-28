package se.sigma.boostapp.boost_app_java.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.sql.Date;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

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
        Step step2 = new Step("4f6648d2-1434-40a5-aa66-7bb07d022841", 600,
                LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T01:00:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"));
        Step step3 = new Step("f3a8948f-1485-49ea-b3a0-e0feb19266af", 200,
                LocalDateTime.parse("2020-01-03T00:00:00"), LocalDateTime.parse("2020-01-03T01:00:00"),
                LocalDateTime.parse("2020-01-03T02:00:00"));
        Step step4 = new Step("93ade648-d7e3-45d1-b8ea-5de4e5a82e95", 800,
                LocalDateTime.parse("2020-01-04T00:00:00"), LocalDateTime.parse("2020-01-04T01:00:00"),
                LocalDateTime.parse("2020-01-04T02:00:00"));
        Step step5 = new Step("4f6648d2-1434-40a5-aa66-7bb07d022841", 1000,
                LocalDateTime.parse("2020-01-05T00:00:00"), LocalDateTime.parse("2020-01-05T01:00:00"),
                LocalDateTime.parse("2020-01-05T02:00:00"));
        stepRepository.save(step);
        stepRepository.save(step2);
        stepRepository.save(step3);
        stepRepository.save(step4);
        stepRepository.save(step5);
    }

    @Test
    public void findByIdAndWithinTimeSpan_Test() {
        List<Step> stepList = stepRepository
                .findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual("4f6648d2-1434-40a5-aa66-7bb07d022841",
                        LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.parse("2020-01-03T23:00:00"));
        assertNotNull(stepList);
        assertThat(stepList.size()).isEqualTo(2);
    }

    @Test
    public void findLatestStepById_Test() {
        Optional<Step> step = stepRepository.findFirstByUserIdOrderByEndTimeDesc("4f6648d2-1434-40a5-aa66-7bb07d022841");
        assertThat(step.isPresent()).isEqualTo(true);
        assertThat(step.get().getStepCount()).isEqualTo(1000);
    }

    @Test
    public void getAllUsers_Test() {
        List<String> userList = stepRepository.getAllUsers();
        assertThat(userList.size()).isEqualTo(3);
    }

    @Test
    public void getStepSum_Test() {
        Optional<Integer> stepSum = stepRepository.getStepCountSum("4f6648d2-1434-40a5-aa66-7bb07d022841",
                LocalDateTime.parse("2019-12-30T23:00:00"), LocalDateTime.parse("2020-01-05T12:00:00"));
        assertThat(stepSum.get()).isEqualTo(2000);
    }

    @Test
    public void getStepCount_test() {
        List<StepDateDTO> listStepCount = stepRepository.getStepCount("4f6648d2-1434-40a5-aa66-7bb07d022841",
                Date.valueOf(LocalDate.parse("2020-01-01")), Date.valueOf(LocalDate.parse("2020-01-05")));
        assertThat(listStepCount.size()).isEqualTo(3);
    }
}
