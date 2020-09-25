package se.sigma.boostapp.boost_app_java.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.sigma.boostapp.boost_app_java.model.Step;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StepRepositoryTest {

    @Autowired
    private StepRepository stepRepository;

    @Before
    public void setUp() {
        Step step = new Step("userA", 400,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));
        Step step2 = new Step("userA", 600,
                LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T01:00:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"));
        Step step5 = new Step("userA", 1000,
                LocalDateTime.parse("2020-01-05T00:00:00"),
                LocalDateTime.parse("2020-01-05T01:00:00"),
                LocalDateTime.parse("2020-01-05T02:00:00"));
        Step step3 = new Step("userB", 200,
                LocalDateTime.parse("2020-01-03T00:00:00"),
                LocalDateTime.parse("2020-01-03T01:00:00"),
                LocalDateTime.parse("2020-01-03T02:00:00"));
        Step step4 = new Step("userC", 800,
                LocalDateTime.parse("2020-01-04T00:00:00"),
                LocalDateTime.parse("2020-01-04T01:00:00"),
                LocalDateTime.parse("2020-01-04T02:00:00"));

        stepRepository.save(step);
        stepRepository.save(step2);
        stepRepository.save(step3);
        stepRepository.save(step4);
        stepRepository.save(step5);
    }

   @Test 
    public void findLatestStepById_Test() {
        Optional<Step> step = stepRepository.findFirstByUserIdOrderByEndTimeDesc("userA");
        assertThat(step.isPresent()).isEqualTo(true);
        assertThat(step.get().getStepCount()).isEqualTo(1000);
    }

    @Test
    public void getAllUsers_Test() {
        List<String> userList = stepRepository.getAllUsers();
        assertThat(userList.size()).isEqualTo(3);
    }


    @Test
    public void findByUserId_test(){
        List<Step> testlist = stepRepository.findByUserId("userA").get();
        assertTrue(3 == testlist.size());
    }

    @Test
    public void deleteAllFromStep_test(){
        stepRepository.deleteAllFromStep();
        var test = stepRepository.getAllUsers();

        assertTrue(test.size() == 0);

    }

}
