package com.nexergroup.boostapp.java.step.repository;

import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.testobjects.model.TestStepBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class StepRepositoryTest {

    @Autowired
    private StepRepository stepRepository;


    TestStepBuilder testStepBuilder = new TestStepBuilder();

    Step step1 = testStepBuilder.createStepOfFirstMinuteOfYear();
    Step step2 = testStepBuilder.createStepOfSecondMinuteOfYear();
    Step step3 = testStepBuilder.createStepOfThirdMinuteOfYear();
    Step nullUserIdStep = testStepBuilder.createStepWhereUserIdIsNull();

    String testUser = "testUser";

    @BeforeEach
    public void setUp() {
        stepRepository.save(step1);
        stepRepository.save(step2);
        stepRepository.save(step3);
        stepRepository.save(nullUserIdStep);
    }

    @Test
    @DisplayName("ok")
    public void testFindFirstMethod_ShouldReturnLatestStep() {
        // Use the method to be tested to retrieve a step from the default test userId
        var step = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser);

        // Expected id of returned Step object
        var expectedId = step3.getId();

        // Actual id of returned object
        var actualId = step.orElseThrow().getId();

        // Assert that the id retrieved Step object is correct
        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Should return list of same length as number of stored user id objects")
    public void testGetListOfAllDistinctUserId_ReturnsListOfCorrectLength() {
        // Call the method to be tested
        var result = stepRepository.getListOfAllDistinctUserId();

        // Expected number of retrieved objects
        var expectedLength = 2;

        // Actual number of retrieved objects
        var actualLength = result.size();

        assertEquals(expectedLength, actualLength);
    }

    @Test
    @DisplayName("Should return list of correct length")
    public void testGetListOfSteps_ReturnsCorrectSize(){
        // Use the method to be tested to fetch Step objects som the default test userId
        var result = stepRepository.getListOfStepsByUserId(testUser).orElseThrow();

        // Expected number of objects
        var expectedLength = 3;

        // Actual number of returned objects
        var actualLength = result.size();

        // Assert length of returned list is equal to number of expected objects
        assertEquals(expectedLength, actualLength);
    }

    @Test
    @DisplayName("Should delete all objects from Step table")
    public void testDeleteAllFromStep_DeletesAll(){
        // Call the method to be tested
        stepRepository.deleteAllFromStep();

        // Assert database is empty
        assertTrue(stepRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Method returns list of correct size")
    public void  testGetStepDataByUserId_ReturnsCorrectLength(){
        // Call the method to be tested to retrieve data from the default test user
        var startTime = Timestamp.valueOf(step1.getEndTime().minusDays(1));
        var endTime = Timestamp.valueOf(step1.getEndTime().plusDays(1));
        var result = stepRepository.getStepDataByUserIdAndDateRange(
                testUser,  startTime, endTime);

        // Expected number of objects retrieved
        var expectedSize = 1;

        // Actual number of retrieved objects
        var actualSize = result.size();

        // Assert that the number of retrieved objects are correct
        assertEquals(expectedSize, actualSize);
    }

    @Test
    @DisplayName("Should return correct stepCount")
    public void testGetStepCountSum_ReturnsCorrectStepCount(){
        // Call the method to be tested to retrieve data from the default test user
        var startTime = step1.getEndTime().minusDays(1);
        var endTime = step1.getEndTime().plusDays(1);
        var result = stepRepository.getStepCountByUserIdAndDateRange(
                testUser, startTime, endTime);

        // Expected stepCount
        var expectedStepCount = 60;

        // Actual stepCount
        var actualStepCount = (int)result.orElseThrow();

        assertEquals(expectedStepCount, actualStepCount);
    }
}