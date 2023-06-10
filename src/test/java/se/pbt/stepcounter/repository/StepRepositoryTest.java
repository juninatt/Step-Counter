package se.pbt.stepcounter.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.pbt.stepcounter.model.Step;
import se.pbt.stepcounter.testobjects.TestObjectBuilder;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("StepRepository:")
public class StepRepositoryTest {

    @Autowired
    private StepRepository stepRepository;


    static TestObjectBuilder testObjectBuilder;

    Step testStep1;
    Step testStep2;
    Step testStep3;
    Step testStep4;

    @BeforeAll
    public static void init() {
        testObjectBuilder = new TestObjectBuilder(2023);
    }

    @BeforeEach
    public void setUp() {
        testStep1 = testObjectBuilder.getTestStep();
        testStep2 = testObjectBuilder.copyAndPostponeMinutes(testStep1, 10);
        testStep3 = testObjectBuilder.copyAndPostponeMinutes(testStep2, 10);
        testStep4 = testObjectBuilder.getTestStep();

        stepRepository.save(testStep1);
        stepRepository.save(testStep2);
        stepRepository.save(testStep3);
        testStep4.setUserId(null);
        stepRepository.save(testStep4);
    }

    @AfterEach
    public void reset() {
        stepRepository.deleteAllFromStep();
    }

    @Nested
    @DisplayName("getListOfStepsByUserId(): ")
    public class GetListOfStepsByUserIdTest {

        @Test
        @DisplayName("Returns list of correct length")
        public void testGetListOfSteps_ReturnsCorrectSize() {
            // Use the method to be tested to fetch Step objects som the default test userId
            var result = stepRepository.getListOfStepsByUserId("testUser").orElseThrow();

            // Expected number of objects
            var expectedLength = 3;

            // Actual number of returned objects
            var actualLength = result.size();

            // Assert length of returned list is equal to number of expected objects
            assertEquals(expectedLength, actualLength);
        }
    }

    @Nested
    @DisplayName("deleteAllFromStep(): ")
    public class DeleteAllFromStepTest {

        @Test
        @DisplayName("Deletes all objects from Step table")
        public void testDeleteAllFromStep_DeletesAll() {
            // Call the method to be tested
            stepRepository.deleteAllFromStep();

            // Assert database is empty
            assertTrue(stepRepository.findAll().isEmpty());
        }
    }

    @Nested
    @DisplayName("getStepCountByUserIdAndDateRange():")
    public class GetStepCountByUserIdAndDateRangeTest {

        @Test
        @DisplayName("Returns correct stepCount")
        public void testGetStepCountSum_ReturnsCorrectStepCount(){
            // Call the method to be tested to retrieve data from the default test user
            var startTime = testStep1.getEndTime().minusDays(1);
            var endTime = testStep1.getEndTime().plusDays(1);
            var result = stepRepository.getStepCountByUserIdAndDateRange(
                    "testUser", startTime, endTime);

            // Expected stepCount
            var expectedStepCount = 39;

            // Actual stepCount
            var actualStepCount = (int)result.orElseThrow();
            System.out.println(stepRepository.findAll().size());
            assertEquals(expectedStepCount, actualStepCount);
        }
    }

    @Nested
    @DisplayName("findFirstByUserIdOrderByStartTimeDesc():")
    public class FindFirstByUserIdOrderByStartTimeDescTest {

        @Test
        @DisplayName("Returns most recent step")
        public void testFindFirstMethod_ShouldReturnLatestStep() {
            // Use the method to be tested to retrieve a step from the default test userId
            var step = stepRepository.findFirstByUserIdOrderByStartTimeDesc("testUser");

            // Expected id of returned Step object
            var expectedId = testStep3.getId();

            // Actual id of returned object
            var actualId = step.orElseThrow().getId();

            // Assert that the id retrieved Step object is correct
            assertEquals(expectedId, actualId);
        }

    }

    @Nested
    @DisplayName("getListOfAllDistinctUserId():")
    public class GetListOfAllDistinctUserIdTest {

        @Test
        @DisplayName("Returns list of same length as number of stored user id objects")
        public void testGetListOfAllDistinctUserId_ReturnsListOfCorrectLength() {
            // Call the method to be tested
            var result = stepRepository.getListOfAllDistinctUserId();

            // Expected number of retrieved objects
            var expectedLength = 2;

            // Actual number of retrieved objects
            var actualLength = result.size();

            assertEquals(expectedLength, actualLength);
        }
    }

    @Nested
    @DisplayName("getStepDataByUserIdAndDateRange():")
    public class GetStepDataByUserIdAndDateRangeTest {

        @Test
        @DisplayName("Returns list of correct size")
        public void  testGetStepDataByUserId_ReturnsCorrectLength(){
            // Call the method to be tested to retrieve data from the default test user
            var startTime = Timestamp.valueOf(testStep1.getEndTime().minusDays(1).toLocalDateTime());
            var endTime = Timestamp.valueOf(testStep1.getEndTime().plusDays(1).toLocalDateTime());
            var result = stepRepository.getStepDataByUserIdAndDateRange(
                    "testUser",  startTime, endTime);

            // Expected number of objects retrieved
            var expectedSize = 1;

            // Actual number of retrieved objects
            var actualSize = result.size();

            // Assert that the number of retrieved objects are correct
            assertEquals(expectedSize, actualSize);
        }
    }
}
