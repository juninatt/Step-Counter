package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.exception.DateTimeValueException;
import com.nexergroup.boostapp.java.step.exception.ValidationFailedException;
import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.service.StepService;
import com.nexergroup.boostapp.java.step.testobjects.dto.stepdto.TestStepDtoBuilder;
import com.nexergroup.boostapp.java.step.testobjects.model.TestStepBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AbstractStepServiceTest {
    @Autowired
    private StepService stepService;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private WeekStepRepository weekStepRepository;
    @Autowired
    private MonthStepRepository monthStepRepository;

    String testUser = "testUser";

    TestStepDtoBuilder testDTOBuilder = new TestStepDtoBuilder();
    TestStepBuilder testStepBuilder = new TestStepBuilder();

    @AfterEach
    public void cleanUp() {
        stepService.deleteStepTable();
        weekStepRepository.deleteAll();
        monthStepRepository.deleteAll();
    }

    @Nested
    @DisplayName("addSingleStepForUser method:")
    public class AddSingleStepForUserTest {

        @Nested
        @DisplayName("Should throw exception: ")
        public class AddSingleStepShouldThrowException {

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when userId-input is null")
            public void testAddSingleStepForUser_ThrowsValidationFailedException_WhenUserIdIsNull() {
                // Create a StepDTO where userId is null to serve as test data
                var testStepDTO = testDTOBuilder.createStepDTOWhereUserIdIsNull();

                // Expected exception message
                var expectedMessage = "userId null. Validation for Step failed";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addSingleStepForUser(null, testStepDTO));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when StepDTO passed to method is null")
            public void testAddSingleStepForUser_ThrowsValidationFailedException_WhenStepDTOIsNull() {
                // Expected exception message
                var expectedMessage = "Step object is empty";

                // Assert that correct exception is thrown when StepDTO passed to method is null
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addSingleStepForUser(testUser, null));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'DateTimeValueException' when StepDTO passed to method has incompatible time-field values")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenTimeValueIsIncorrect() {
                // Create a StepDTO where time-fields are incompatible as test data
                var badTestDto = testDTOBuilder.createStepDTOWhereTimeFieldsAreIncompatible();

                // Expected exception message
                var expectedMessage = "Start time must be before end time";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        DateTimeValueException.class, () -> stepService.addSingleStepForUser(testUser, badTestDto));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when StepDTO startTime is null")
            public void testAddSingleStepForUser_ThrowsValidationFailedException_WhenStartTimeIsNull() {
                // Create a StepDTO where startTime is null
                var badTestDto = testDTOBuilder.createStepDTOWhereStartTimeIsNull();

                // Expected exception message
                var expectedMessage = "Step start time was null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addSingleStepForUser(testUser, badTestDto));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when StepDTO endTime is null")
            public void testAddSingleStepForUser_ThrowsValidationFailedException_WhenEndTimeIsNull() {
                // Create a StepDTO where endTime is null as test data
                var badTestDto = testDTOBuilder.createStepDTOWhereEndTimeIsNull();

                // Expected exception message
                var expectedMessage = "End time was null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addSingleStepForUser(testUser, badTestDto));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when StepDTO uploadTime is null")
            public void testAddSingleStepForUser_ThrowsValidationFailedException_WhenUploadTimeIsNull() {
                // Create a StepDTO where uploadTime is null as test data
                var badTestDto = testDTOBuilder.createStepDTOWhereUploadTimeIsNull();

                // Expected exception message
                var expectedMessage = "Upload time is null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addSingleStepForUser(testUser, badTestDto));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }
        }

        @Nested
        @DisplayName("Should return correct values")
        public class AddSingleStepShouldReturnCorrectValues {

            @Nested
            @DisplayName("With empty database")
            public class AddSingleStepEmptyDataBase {

                @BeforeEach
                void setup() {
                    dropAllTables();
                    assertTrue(dataBaseIsEmpty());
                }

                @Test
                @DisplayName("Should return Step of Step class when input is valid")
                public void testAddSingleStepForUser_ReturnsStepOfStepDtoClass_WhenInputIsValid() {

                    // Create a StepDTO to serve as test data
                    var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testDto);

                    // Expected class of returned object
                    var expectedClass = Step.class;

                    // Actual class of returned object
                    var actualClass = result.getClass();

                    // Assert that the returned object is of the correct class
                    assertEquals(expectedClass, actualClass,
                            () -> "Expected class to be '" + expectedClass + "' but was " + result.getClass());
                }

                @Test
                @DisplayName("Should return Step with correct userId when input is valid")
                public void testAddSingleStepForUser_ReturnsStepWithCorrectUserId_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testDto);

                    // Expected userId of the returned object
                    var expectedUserId = testUser;

                    // Actual userId of the returned object
                    var actualUserId = result.getUserId();

                    // Assert that the returned object has the correct userId
                    assertEquals(expectedUserId, actualUserId,
                            "Expected userId to be '" + expectedUserId + "'  but was '" + actualUserId + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with correct stepCount when input is valid")
                public void testAddSingleStepForUser_ReturnsStepWithCorrectStepCount_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected stepCount of the returned Step
                    var expectedStepCount = testStepDTO.getStepCount();

                    // Actual stepCount of the returned Step
                    var actualStepCount = result.getStepCount();

                    // Assert that the returned Step has the correct endTime
                    assertEquals(expectedStepCount, actualStepCount,
                            "Expected stepCount to be '" + expectedStepCount + "'  but was '" + actualStepCount + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with correct startTime when input is valid")
                public void testAddSingleStepForUser_ReturnsStepWithCorrectStartTime_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testDto);

                    // Expected startTime of the returned object
                    var expectedStartTime = testDto.getStartTime();

                    // Actual startTime of the returned object
                    var actualStartTime = result.getStartTime();

                    // Assert that the returned object has the correct startTime
                    assertEquals(expectedStartTime, actualStartTime,
                            "Expected startTime to be '" + expectedStartTime + "'  but was '" + actualStartTime + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with correct endTime when input is valid")
                public void testAddSingleStepForUser_ReturnsStepWithCorrectEndTime_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testDto);

                    // Expected endTime of the returned object
                    var expectedEndTime = testDto.getEndTime();

                    // Actual endTime of the returned object
                    var actualEndTime = result.getEndTime();

                    // Assert that the returned object has the correct endTime
                    assertEquals(expectedEndTime, actualEndTime,
                            "Expected endTime to be '" + expectedEndTime + "'  but was '" + actualEndTime + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with correct uploadTime when input is valid")
                public void testAddSingleStepForUser_ReturnsStepWithCorrectUploadTime_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testDto);

                    // Expected uploadTime of the returned object
                    var expectedUploadTime = testDto.getUploadTime();

                    // Actual uploadTime of the returned object
                    var actualUploadTime = result.getUploadedTime();

                    // Assert that the returned object has the correct endTime
                    assertEquals(expectedUploadTime, actualUploadTime,
                            "Expected endTime to be '" + expectedUploadTime + "'  but was '" + actualUploadTime + "'. " + result);
                }
            }

            @Nested
            @DisplayName("With data in database")
            public class AddSingleStepWithDataInDataBase {

                // Create a test Step object
                Step testStep = testStepBuilder.createStepOfFirstMinuteOfYear();

                @BeforeEach
                void setup() {
                    // Make sure database is empty
                    assertTrue(dataBaseIsEmpty());
                    // Save the created test Step object to all tables in database
                    saveToAllTables(testStep);
                }

                @AfterEach
                void cleanUp() {
                    dropAllTables();
                }

                @Test
                @DisplayName("Should return Step with correct userId when input is valid")
                public void testAddSingleStepForUser_ReturnsStepWithCorrectUserId_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                    // Set time field so Step in database gets updated
                    testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected userId of the returned Step
                    var expectedUserId = testUser;

                    // Actual userId of the returned Step
                    var actualUserId = result.getUserId();

                    // Assert that the returned Step has the correct userId
                    assertEquals(expectedUserId, actualUserId,
                            "Expected userId to be '" + expectedUserId + "'  but was '" + actualUserId + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with updated startTime when input is valid and new data starts before old Step ends")
                public void testAddSingleStepForUser_ReturnsStepWithUpdatedStartTime_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                    // Set time field so Step in database gets updated
                    testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected startTime of the returned Step
                    var expectedStartTime = testStepDTO.getStartTime();

                    // Actual startTime of the returned Step
                    var actualStartTime = result.getStartTime();

                    // Assert that the returned Step has the correct startTime
                    assertEquals(expectedStartTime, actualStartTime,
                            "Expected startTime to be '" + expectedStartTime + "'  but was '" + actualStartTime + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with updated endTime when input is valid and new data starts before old Step ends")
                public void testAddSingleStepForUser_ReturnsStepWithUpdatedEndTime_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                    // Set time field so Step in database gets updated
                    testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected endTime of the returned Step
                    var expectedEndTime = testStepDTO.getEndTime();

                    // Actual endTime of the returned Step
                    var actualEndTime = result.getEndTime();

                    // Assert that the returned Step has the correct endTime
                    assertEquals(expectedEndTime, actualEndTime,
                            "Expected endTime to be '" + expectedEndTime + "'  but was '" + actualEndTime + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with updated uploadTime when input is valid and new data starts before old Step ends")
                public void testAddSingleStepForUser_ReturnsStepWithUpdatedUploadTime_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                    // Set time field so Step in database gets updated
                    testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected uploadTime of the returned Step
                    var expectedUploadTime = testStepDTO.getUploadTime();

                    // Actual uploadTime of the returned Step
                    var actualUploadTime = result.getUploadedTime();

                    // Assert that the returned Step has the correct uploadTime
                    assertEquals(expectedUploadTime, actualUploadTime,
                            "Expected uploadTime to be '" + expectedUploadTime + "'  but was '" + actualUploadTime + "'. " + result);
                }

                @Test
                @DisplayName("Should return Step with updated stepCount when input is valid and new data starts before old Step ends")
                public void testAddSingleStepForUser_ReturnsStepWithUpdatedStepCount_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                    // Set time field so Step in database gets updated
                    testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected stepCount of the returned Step
                    var expectedStepCount = testStepDTO.getStepCount() + testStep.getStepCount();

                    // Actual stepCount of the returned Step
                    var actualStepCount = result.getStepCount();

                    // Assert that the returned Step has the correct endTime
                    assertEquals(expectedStepCount, actualStepCount,
                            "Expected stepCount to be '" + expectedStepCount + "'  but was '" + actualStepCount + "'. " + result);
                }

                @Test
                @DisplayName("Should return the updated Step object when input is valid and new data starts before old Step ends")
                public void testAddSingleStepForUser_ReturnsUpdatedStep_WhenInputIsValid() {
                    // Create a StepDTO to serve as test data
                    var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                    // Set time field so Step in database gets updated
                    testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                    // Pass the test data to the method to be tested
                    var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected id of the returned Step
                    var expectedStepId = testStep.getId();

                    // Actual id of the returned Step
                    var actualStepId = result.getId();

                    // Assert that the returned Step has the correct id
                    assertEquals(expectedStepId, actualStepId,
                            "Expected id of Step object to be '" + expectedStepId + "'  but was '" + actualStepId + "'. " + result);
                }
            }
        }

        @Nested
        @DisplayName("Should update Step objects in database correctly")
        public class AddSingleStepShouldUpdateStepObjectsCorrectly {

            // Create a test Step object
            Step testStep = testStepBuilder.createStepOfFirstMinuteOfYear();

            @BeforeEach
            void setup() {
                // Make sure database is empty
                assertTrue(dataBaseIsEmpty());
                // Save the created test Step object to all tables in database
                saveToAllTables(testStep);
            }

            @AfterEach
            void cleanUp() {
                dropAllTables();
            }

            @Test
            @DisplayName("Should not create new Step object if startTime of StepDTO is before endTime of Step")
            public void testAddSingleStepForUser_DoesNotCreateNewStep_WhenStartTimeIsBeforeEndTime() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                // Set time field so Step in database gets updated
                testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected number of objects in database
                var expectedNumberOfObjects = 1;

                // Actual number of objects in database
                var actualNumberOfObjects = stepRepository.findAll().size();

                // Assert that a new object was not added to the database
                assertEquals(expectedNumberOfObjects, actualNumberOfObjects,
                        "Expected number of objects to be '" + expectedNumberOfObjects + "'  but was '" + actualNumberOfObjects);
            }

            @Test
            @DisplayName("Should update stepCount of Step in database if input is valid and new data starts before old Step ends")
            public void testAddSingleStepForUser_UpdatesStepCountOfStep_IfStepIsFoundInDataBase() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                // Set time field so Step in database gets updated
                testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected stepCount after Step is updated
                var expectedStepCount = testStepDTO.getStepCount() + testStep.getStepCount();

                // Actual object in database and stepCount
                var result = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser);
                var actualStepCount = result.orElseThrow().getStepCount();

                // Assert that stepCount of Step has been updated correctly
                assertEquals(expectedStepCount, actualStepCount,
                        "Expected stepCount to be '" + expectedStepCount + "'  but was '" + actualStepCount + "'. " + result);
            }

            @Test
            @DisplayName("Should update endTime of Step in database if input is valid and new data starts before old Step ends")
            public void testAddSingleStepForUser_UpdatesEndTimeOfStep_IfStepIsFoundInDataBase() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                // Set time field so Step in database gets updated
                testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected endTime after Step is updated
                var expectedEndTime = testStepDTO.getEndTime();

                // Actual object in database and endTime
                var result = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser);
                var actualEndTime = result.orElseThrow().getEndTime();

                // Assert that endTime of Step has been updated correctly
                assertEquals(expectedEndTime, actualEndTime,
                        "Expected endTime to be '" + expectedEndTime + "'  but was '" + actualEndTime + "'. " + result);
            }

            @Test
            @DisplayName("Should update uploadTime of Step in database if input is valid and new data starts before old Step ends")
            public void testAddSingleStepForUser_UpdatesUploadTimeOfStep_IfStepIsFoundInDataBase() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                // Set time field so Step in database gets updated
                testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected uploadTime after Step is updated
                var expectedUploadTime = testStepDTO.getUploadTime();

                // Actual object in database and uploadTime
                var result = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser);
                var actualUploadTime = result.orElseThrow().getUploadedTime();

                // Assert that uploadTime of Step has been updated correctly
                assertEquals(expectedUploadTime, actualUploadTime,
                        "Expected uploadTime to be '" + expectedUploadTime + "'  but was '" + actualUploadTime + "'. " + result);
            }
        }

        @Nested
        @DisplayName("Should update WeekStep objects in database correctly")
        public class AddSingleStepShouldUpdateWeekStepObjectsCorrectly {

            // Create a test Step object
            Step testStep = testStepBuilder.createStepOfFirstMinuteOfYear();

            @BeforeEach
            void setup() {
                // Make sure database is empty
                assertTrue(dataBaseIsEmpty());
                // Save the created test Step object to all tables in database
                saveToAllTables(testStep);
            }

            @AfterEach
            void cleanUp() {
                dropAllTables();
            }

            @Test
            @DisplayName("Should update stepCount of WeekStep object if input is valid and new data starts before old Step ends")
            public void testAddingSingleStepForUser_UpdatesStepCountInWeekStepTable_IfDataExistsInDataBase() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
                // Set time field so Step in database gets updated
                testStepDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected stepCount of WeekStep object in database
                var expectedStepCount = testStepDTO.getStepCount() + testStep.getStepCount();

                // Actual object in database and stepCount
                // TODO: Use new query
                var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, 2023, 1);
                var actualStepCount = result.orElseThrow().getStepCount();

                // Assert that the stepCount of the WeekStep has been updated correctly
                assertEquals(expectedStepCount, actualStepCount,
                        () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + result);
            }
        }

        @Nested
        @DisplayName("Should create new records in database correctly")
        public class AddSingleStepShouldCreateNewObjects {
                @BeforeEach
                void setup() {
                    // Delete database and assert that it is empty
                    dropAllTables();
                }

                @Test
                @DisplayName("Should create new Step if no Step is found in database")
                public void testAddSingleStepForUser_CreatesNewStep_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Assert that Step table is not empty
                    assertFalse(stepRepository.findAll().isEmpty());
                }

                @Test
                @DisplayName("Should create new WeekStep if no Step is found in database")
                public void testAddSingleStepForUser_CreatesNewWeekStep_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Assert that WeekStep table is not empty
                    assertFalse(weekStepRepository.findAll().isEmpty());
                }

                @Test
                @DisplayName("Should create new MonthStep if no Step is found in database")
                public void testAddSingleStepForUser_CreatesNewMonthStep_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Assert that MonthStep is not empty
                    assertFalse(monthStepRepository.findAll().isEmpty());
                }

            @Test
            @DisplayName("Should create new Step with correct stepCount if no Step is found in database")
            public void testAddSingleStepForUser_CreatesStepWithCorrectStepCount_IfNoStepIsFoundInDataBase() {
                // Make sure database is empty and create a StepDTO to serve as test data
                assertTrue(dataBaseIsEmpty());
                var testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected stepCount
                var expectedStepCount = testStepDTO.getStepCount();

                // Actual object in database and stepCount
                var result = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser);
                var actualStepCount = result.orElseThrow().getStepCount();

                // Assert that object saved to the database has the correct stepCount
                assertEquals(expectedStepCount, actualStepCount,
                        () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + result);
            }

            @Test
            @DisplayName("Should create new WeekStep with correct stepCount if no Step is found in database")
            public void testAddSingleStepForUser_CreatesWeekStepWithCorrectStepCount_IfNoStepIsFoundInDataBase() {
                // Make sure database is empty and create a StepDTO to serve as test data
                assertTrue(dataBaseIsEmpty());
                var testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected stepCount
                var expectedStepCount = testStepDTO.getStepCount();

                // Actual object in database and its stepCount
                // TODO: Use new query
                var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, 2023, 1);
                var actualStepCount = result.orElseThrow().getStepCount();

                // Assert that object saved to the database has the correct stepCount
                assertEquals(expectedStepCount, actualStepCount,
                        () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + result);
                }
                @Test
                @DisplayName("Should create new MonthStep with correct stepCount if no Step is found in database")
                public void testAddSingleStepForUser_CreatesMonthStepWithCorrectStepCount_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected stepCount
                    var expectedStepCount = testStepDTO.getStepCount();

                    // Actual object in database and its stepCount
                    // TODO: Use new query
                    var result = monthStepRepository.findByUserIdAndYearAndMonth(testUser, 2023, 1);
                    var actualStepCount = result.orElseThrow().getStepCount();

                    // Assert that object saved to the database has the correct stepCount
                    assertEquals(expectedStepCount, actualStepCount,
                            () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + result);
                }
                @Test
                @DisplayName("Should create objects for all tables with equal stepCount when no step exists for user in database")
                public void testAddingSingleStepForUser_AddsStepCountToAllTables()  {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Expected stepCount
                    var expectedStepCount = testStepDTO.getStepCount();

                    // Actual objects in database and their stepCount
                    var storedStep = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser);
                    var actualStepCount = storedStep.orElseThrow().getStepCount();
                    // TODO: Use new query
                    var storedWeekStep = weekStepRepository.findByUserIdAndYearAndWeek(testUser, 2023, 1);
                    var actualWeekStepCount = storedWeekStep.orElseThrow().getStepCount();
                    var storedMonthStep = monthStepRepository.findByUserIdAndYearAndMonth(testUser, 2023, 1);
                    var actualMonthStepCount = storedMonthStep.orElseThrow().getStepCount();

                    // Assert that a Step object with the correct stepCount was added to the database
                    assertEquals(expectedStepCount, actualStepCount,
                            () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + storedStep);
                    // Assert that a WeekStep object with the correct stepCount was added to the database
                    assertEquals(expectedStepCount, actualWeekStepCount,
                            () -> "Expected stepCount in WeekStep table to be '" + expectedStepCount + "' but was '" + actualWeekStepCount + "'. " + storedWeekStep);
                    // Assert that a MonthStep object with the correct stepCount was added to the database
                    assertEquals(expectedStepCount, actualMonthStepCount,
                            () -> "Expected stepCount in MonthStep table to be '" + expectedStepCount + "' but was '" + actualMonthStepCount + "'. " + storedMonthStep);
                }

                @Test
                @DisplayName("Should add all fields of the Step object to the database correctly")
                public void testAddSingleStepForUser_AddsAllFieldsCorrectlyTStepTable() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testDto);

                    // Expected values of object fields
                    var expectedUserId = testUser;
                    var expectedStepCount = testDto.getStepCount();
                    var expectedStartTime = testDto.getStartTime();
                    var expectedEndTime = testDto.getEndTime();
                    var expectedUploadTime = testDto.getUploadTime();

                    // Actual values of object stored in database
                    var result = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser).orElseThrow();
                    var actualUserId = result.getUserId();
                    var actualStepCount = result.getStepCount();
                    var actualStartTime = result.getStartTime();
                    var actualEndTime = result.getEndTime();
                    var actualUploadTime = result.getUploadedTime();

                    // Assert that all field values got saved correctly
                    assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but was " + actualUserId);
                    assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount);
                    assertEquals(expectedStartTime, actualStartTime, "Expected startTime to be '" + expectedStartTime + "' but was " + actualStartTime);
                    assertEquals(expectedEndTime, actualEndTime, "Expected endTime to be '" + expectedEndTime + "' but was " + actualEndTime);
                    assertEquals(expectedUploadTime, actualUploadTime, "Expected uploadTime to be '" + expectedUploadTime + "' but was " + actualUploadTime);
                }

                @Test
                @DisplayName("Should store all WeekStep-fields in database correctly when database is empty")
                public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToWeekStepTable() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testDto);

                    // Expected values of object stored in database
                    var expectedUserId = testUser;
                    var expectedStepCount = testDto.getStepCount();
                    var expectedYear = testDto.getYear();
                    var expectedWeek = DateHelper.getWeek(testDto.getEndTime());

                    // Actual values of the object stored in the database
                    var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, 2023, 1).orElseThrow();
                    var actualUserId = result.getUserId();
                    var actualYear = result.getYear();
                    var actualWeek = result.getWeek();
                    var actualStepCount = result.getStepCount();

                    // Assert that all field values got saved correctly
                    assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but was " + actualUserId);
                    assertEquals(expectedYear, actualYear, "Expected year to be '" + expectedYear + "' but was " + actualYear);
                    assertEquals(expectedWeek, actualWeek, "Expected week value to be '" + expectedWeek + "' but was " + actualWeek);
                    assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount);
                }

            @Test
            @DisplayName("Should store all MonthStep-fields in database correctly when database is empty")
            public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToMonthStepTable() {
                // Make sure database is empty and create a StepDTO to serve as test data
                assertTrue(dataBaseIsEmpty());
                var testDto = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testDto);

                // Expected values of object stored in database
                var expectedUserId = testUser;
                var expectedStepCount = testDto.getStepCount();
                var expectedYear = testDto.getYear();
                var expectedMonth = testDto.getEndTime().getMonthValue();

                // Actual values of the object stored in the database
                var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, 2023, 1).orElseThrow();
                var actualUserId = result.getUserId();
                var actualStepCount = result.getStepCount();
                var actualYear = result.getYear();
                var actualMonth = result.getWeek();

                // Assert that all field values got saved correctly
                assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but was " + actualUserId);
                assertEquals(expectedYear, actualYear, "Expected year to be '" + expectedYear + "' but was " + actualYear);
                assertEquals(expectedMonth, actualMonth, "Expected month value to be '" + expectedMonth + "' but was " + actualMonth);
                assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount);
            }
            }
        }


    @Nested
    @DisplayName("addMultipleStepsForUser method: ")
    public class AddMultipleStepsForUserTest {

        // Create a list to hold the test data
        List<StepDTO> testStepDTOList = new ArrayList<>();

        @BeforeEach
        public void setUp() {
            // Create and add StepDTO:s to the test list
            testStepDTOList.add(testDTOBuilder.createStepDTOOfFirstMinuteOfYear());
            testStepDTOList.add(testDTOBuilder.createStepDTOOfSecondMinuteOfYear());
            testStepDTOList.add(testDTOBuilder.createStepDTOOfThirdMinuteOfYear());
        }

        @Nested
        @DisplayName("Should throw exception")
        public class AddMultipleStepsShouldThrowException {

            @Test
            @DisplayName("Should throw 'ValidationFailedException'' when userId passed to method is null")
            public void testAddMultipleStepsForUser_ThrowsValidationFailedException_WhenUserIdInputIsNull() {
                // Expected exception message
                var expectedMessage = "Validation of new data failed";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addMultipleStepsForUser(null, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException'' when list passed to method is null")
            public void  testAddMultipleStepsForUser_ThrowsValidationFailedException_WhenListIsNull() {
                // Expected exception message
                var expectedMessage = "Validation of new data failed";
                // Assert that correct exception is thrown when the list passed to the method is null
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addMultipleStepsForUser(testUser, null));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'DateTimeValueException' when list passed to method contains StepDTO with incompatible time fields")
            public void testAddMultipleStepsForUser_ThrowsDateTimeValueException_WhenListContainsBadTimeFields() {
                // Create StepDTO with incompatible time fields and add to test list
                var badTimeFieldsDTO = testDTOBuilder.createStepDTOWhereTimeFieldsAreIncompatible();
                testStepDTOList.add(badTimeFieldsDTO);

                // Expected exception message
                var expectedMessage = "Start time must be before end time";

                // Assert that correct exception is thrown when the list passed to the method is null
                var result = assertThrows(
                        DateTimeValueException.class, () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '"+ result.getMessage() + "'");
                testStepDTOList.remove(badTimeFieldsDTO);
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when list contains StepDTO with null startTime")
            public void testMultipleStepsForUser_ThrowsValidationFailedException_WhenStartTimeIsNull() {
                // Create a StepDTO where startTime is null
                var badTestDto = testDTOBuilder.createStepDTOWhereStartTimeIsNull();
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "Step start time was null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when StepDTO endTime is null")
            public void testAddSingleStepForUser_ThrowsValidationFailedException_WhenEndTimeIsNull() {
                // Create a StepDTO where endTim is null
                var badTestDto = testDTOBuilder.createStepDTOWhereEndTimeIsNull();
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "End time was null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }

            @Test
            @DisplayName("Should throw 'ValidationFailedException' when StepDTO uploadTime is null")
            public void testAddSingleStepForUser_ThrowsValidationFailedException_WhenUploadTimeIsNull() {
                // Create a StepDTO where uploadTime is null
                var badTestDto = testDTOBuilder.createStepDTOWhereUploadTimeIsNull();
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "Upload time is null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        ValidationFailedException.class, () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }
        }

        @Nested
        @DisplayName("Should return correct values")
        public class AddMultipleStepsShouldReturnCorrectValues {
            @BeforeEach
            void setUp() {
                dropAllTables();
            }

            @Test
            @DisplayName("Should return a Step object with the total stepCount of the StepDTO:s in the list passed as input")
            public void testAddMultipleStepsForUser_ReturnsStepWithTotalStepCount_WhenNoUserExistsInDatabase() {
                // Make sure the database is empty and pass the list holding the test data to the method to be tested
                assertTrue(dataBaseIsEmpty());
                var result = stepService.addMultipleStepsForUser(testUser, testStepDTOList);

                // Expected total sum of the stepCount of the test data
                int expectedStepCount = testStepDTOList.stream()
                        .mapToInt(StepDTO::getStepCount)
                        .sum();

                // Actual sum of the stepCount from the test data
                int actualStepCount = result.getStepCount();

                // Assert that the Step object returned by the tested method has the correct stepCount
                assertEquals(expectedStepCount, actualStepCount,"Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + result);
            }
        }
    }

    @Nested
    @DisplayName("Parameterized tests: ")
    public class ParameterizedTests {

        @DisplayName("Add Single Step For User Validation Tests")
        @ParameterizedTest(name = "When adding a step with userId={0}, ValidationFailedException is thrown: {1}")
        @CsvSource({
                "'', true",
                "1, false",
        })
        public void testAddSingleStepForUser_ThrowsValidationFailedException(String userId, boolean shouldThrow) {
            StepDTO testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
            if (shouldThrow) {
                assertThrows(ValidationFailedException.class, () -> stepService.addSingleStepForUser(userId.isEmpty() ? null : userId, testStepDTO));
            } else {
                assertDoesNotThrow(() -> stepService.addSingleStepForUser(userId.isEmpty() ? null : userId, testStepDTO));
            }
        }
    }

    private void saveToAllTables(Step step) {
        stepRepository.save(step);
        weekStepRepository.save(testStepBuilder.createWeekStepOfStep(step));
        monthStepRepository.save(testStepBuilder.createMonthStepOfStep(step));
    }
    private boolean dataBaseIsEmpty() {
        return stepRepository.findAll().isEmpty()
                && weekStepRepository.findAll().isEmpty()
                && monthStepRepository.findAll().isEmpty();
    }
    private void dropAllTables() {
        stepRepository.deleteAllFromStep();
        weekStepRepository.deleteAll();
        monthStepRepository.deleteAll();
    }
}
