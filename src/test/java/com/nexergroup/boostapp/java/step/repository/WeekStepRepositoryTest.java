package com.nexergroup.boostapp.java.step.repository;

import com.nexergroup.boostapp.java.step.model.WeekStep;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

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
        WeekStep wStep5 = new WeekStep("gabrielle", 52,2020, 900);
        WeekStep wStep6 = new WeekStep("gabrielle", 52,2020, 900);

        weekStepRepository.save(wStep1);
        weekStepRepository.save(wStep2);
        weekStepRepository.save(wStep3);
        weekStepRepository.save(wStep4);
        weekStepRepository.save(wStep5);
        weekStepRepository.save(wStep6);
    }

    @Test
    public void shouldReturnWeekStepForWeek1Year2020() {

        var test = weekStepRepository.findByUserIdAndYearAndWeek("johanna", 2020, 1).orElseThrow();
        Assert.assertEquals(300, test.getStepCount());

    }

    @Test
    public void shouldReturnAllWeekStepsFromYear2020() {
        List<WeekStep> weekSteps = weekStepRepository.getAllWeekStepsFromYearForUser(2020, "johanna");
        Assert.assertEquals(1, weekSteps.size());
    }

    @Test
    public void shouldReturnEmptyOptionalForNonExistingWeekStep() {
        Optional<WeekStep> weekStep = weekStepRepository.findByUserIdAndYearAndWeek("john", 2021, 2);
        Assert.assertFalse(weekStep.isPresent());
    }

    @Test
    public void shouldSetTotalStepCountById() {
        Optional<WeekStep> weekStepOptional = weekStepRepository.findTopByUserIdOrderByIdDesc("johanna");
        WeekStep weekStep = weekStepOptional.orElseThrow();

        weekStep.setStepCount(500);
        WeekStep updatedWeekStep = weekStepRepository.save(weekStep);

        Assert.assertEquals(500, updatedWeekStep.getStepCount());
    }



    @Test
    public void shouldDeleteWeekStepById() {
        WeekStep weekStep = new WeekStep("testUser", 1, 2023, 1000);
        WeekStep savedWeekStep = weekStepRepository.save(weekStep);

        weekStepRepository.deleteById(savedWeekStep.getId());

        Optional<WeekStep> deletedWeekStep = weekStepRepository.findById(savedWeekStep.getId());
        Assert.assertFalse(deletedWeekStep.isPresent());
    }

    @Test
    public void shouldUpdateStepCountForExistingWeekStepId() {
        // Create a WeekStep object and save it to the database
        WeekStep weekStep = new WeekStep("johanna", 1, 2023, 100);
        WeekStep savedWeekStep = weekStepRepository.save(weekStep);

        // Call the setTotalStepCountById method to update the step count
        savedWeekStep.setStepCount(500);
        weekStepRepository.save(savedWeekStep);

        // Retrieve the updated WeekStep object from the database
        WeekStep updatedWeekStep = weekStepRepository.findById(savedWeekStep.getId()).orElseThrow();

        // Assert that the step count has been updated correctly
        Assert.assertEquals(500, updatedWeekStep.getStepCount());
    }

    @Test
    public void shouldUpdateStepCountToZero() {
        // Create a WeekStep object and save it to the database
        WeekStep weekStep = new WeekStep("johanna", 1, 2023, 100);
        WeekStep savedWeekStep = weekStepRepository.save(weekStep);

        // Call the setTotalStepCountById method to update the step count to zero
        weekStep.setStepCount(0);
        weekStepRepository.save(weekStep);

        // Retrieve the updated WeekStep object from the database
        WeekStep updatedWeekStep = weekStepRepository.findById(savedWeekStep.getId()).orElseThrow();

        // Assert that the step count has been updated correctly (zero)
        Assert.assertEquals(0, updatedWeekStep.getStepCount());
    }

    @Test
    public void shouldReturnWeekStepsForUserAndYear() {
        List<WeekStep> weekSteps = weekStepRepository.getAllWeekStepsFromYearForUser(2020, "johanna");
        Assert.assertEquals(1, weekSteps.size());
        Assert.assertEquals("johanna", weekSteps.get(0).getUserId());
        Assert.assertEquals(2020, weekSteps.get(0).getYear());
    }

    @Test
    public void shouldReturnEmptyListForNonExistingUser() {
        List<WeekStep> weekSteps = weekStepRepository.getAllWeekStepsFromYearForUser( 2020, "no one");
        Assert.assertTrue(weekSteps.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListForNonExistingYear() {
        List<WeekStep> weekSteps = weekStepRepository.getAllWeekStepsFromYearForUser(2021, "johanna");
        Assert.assertTrue(weekSteps.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListForNonExistingUserAndYear() {
        List<WeekStep> weekSteps = weekStepRepository.getAllWeekStepsFromYearForUser( 2021, "no one");
        Assert.assertTrue(weekSteps.isEmpty());
    }
    @Test
    public void shouldReturnCorrectListSizeWithSeveralObjectsInDB() {
        List<WeekStep> weekSteps = weekStepRepository.getAllWeekStepsFromYearForUser( 2020, "gabrielle");
        var secondResult = weekStepRepository.getStepCountByUserIdYearAndWeek("gabrielle", 2020, 52)
                .orElseThrow();
        Assert.assertEquals(3, weekSteps.size());
        Assert.assertEquals(1800, (int) secondResult);
    }
}
