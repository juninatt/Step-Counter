package se.pbt.stepcounter.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.exception.DateTimeValueException;
import se.pbt.stepcounter.exception.ValidationFailedException;
import se.pbt.stepcounter.mapper.DateHelper;
import se.pbt.stepcounter.model.Step;
import se.pbt.stepcounter.repository.MonthStepRepository;
import se.pbt.stepcounter.repository.StepRepository;
import se.pbt.stepcounter.repository.WeekStepRepository;
import se.pbt.stepcounter.testobjects.dto.stepdto.TestStepDtoBuilder;
import se.pbt.stepcounter.testobjects.model.TestStepBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StepServiceTest {
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
                var expectedMessage = "Object holding new data cant be null";

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
            @DisplayName("Should throw 'DateTimeValueException' when StepDTO startTime is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenStartTimeIsNull() {
                // Create a StepDTO where startTime is null
                var badTestDto = testDTOBuilder.createStepDTOWhereStartTimeIsNull();

                // Expected exception message
                var expectedMessage = "Start time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        DateTimeValueException .class, () -> stepService.addSingleStepForUser(testUser, badTestDto));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'DateTimeValueException' when StepDTO endTime is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenEndTimeIsNull() {
                // Create a StepDTO where endTime is null as test data
                var badTestDto = testDTOBuilder.createStepDTOWhereEndTimeIsNull();

                // Expected exception message
                var expectedMessage = "End time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        DateTimeValueException.class, () -> stepService.addSingleStepForUser(testUser, badTestDto));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Should throw 'DateTimeValueException' when StepDTO uploadTime is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenUploadTimeIsNull() {
                // Create a StepDTO where uploadTime is null as test data
                var badTestDto = testDTOBuilder.createStepDTOWhereUploadTimeIsNull();

                // Expected exception message
                var expectedMessage = "Upload time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        DateTimeValueException.class, () -> stepService.addSingleStepForUser(testUser, badTestDto));
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
                    var actualUploadTime = result.getUploadTime();

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
                    var actualUploadTime = result.getUploadTime();

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
                    var expectedStepCount = testStepDTO.getStepCount();

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

                    // Assert that the returned Step has the correct id and stepCount
                    assertEquals(expectedStepId, actualStepId,
                            "Expected id of Step object to be '" + expectedStepId + "'  but was '" + actualStepId + "'. " + result);
                    assertEquals(testStepDTO.getStepCount(), result.getStepCount());
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
                // Get end-time of the step stored in the database
                var testStepEndTime = testStep.getEndTime();

                // Create a StepDTO to serve as test data that starts before the database step ends
                var testStepDTO = new StepDTO(testUser, 1000, testStepEndTime.minusMinutes(1), testStepEndTime.plusMinutes(4), testStepEndTime.plusMinutes(5));

                // Pass the StepDTO to the method to be tested to store the new data in the database
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
                var expectedStepCount = testStepDTO.getStepCount();

                // Actual object in database and stepCount
                var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);
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
                var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);
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
                var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);
                var actualUploadTime = result.orElseThrow().getUploadTime();

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
            @DisplayName("Should update stepCount of WeekStep object if input is valid")
            public void   testAddingSingleStepForUser_UpdatesStepCountInWeekStepTable_IfDataExistsInDataBase() {
                // Get current time
                var now = ZonedDateTime.now();
                var currentWeek = DateHelper.getWeek(now);

                // Create and save step to database
                var testDTO1 = new StepDTO(testUser, 10, now.plusMinutes(1), now.plusMinutes(2), now.plusMinutes(3));
                stepService.addSingleStepForUser(testUser, testDTO1);


                // Create a StepDTO to serve as test data
                var testDTO2 = new StepDTO(testUser, 59, now.plusMinutes(3), now.plusMinutes(4), now.plusMinutes(5));

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, testDTO2);

                // Expected stepCount of WeekStep object in database
                var expectedStepCount = testDTO2.getStepCount();

                // Actual object in database and stepCount
                var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, 2023, currentWeek);
                var actualStepCount = result.orElseThrow().getStepCount();

                // Assert that the stepCount of the WeekStep has been updated correctly
                assertEquals(expectedStepCount, actualStepCount,
                        () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + result);
            }

            @Test
            @DisplayName("Should overwrite stepCount value in database when new DTO is added")
            public void testAddingSingleStepForUser_CreatesOneWeekStepObject_IfStepObjectsAreSameMonth() {
                cleanUp();
                // Get the value for the start of the week
                var startOfWeek = DateHelper.getWeekStart(ZonedDateTime.now());

                // Create DTOs to add and call test method efter each
                var testDTO1 = new StepDTO(testUser, 10,  startOfWeek.plusMinutes(2), startOfWeek.plusMinutes(12), startOfWeek.plusMinutes(56));
                stepService.addSingleStepForUser(testUser, testDTO1);
                System.out.println(weekStepRepository.findAll().get(0).getStepCount());

                var testDTO2 = new StepDTO(testUser, 20,  startOfWeek.plusMinutes(58), startOfWeek.plusMinutes(68), startOfWeek.plusMinutes(78));
                stepService.addSingleStepForUser(testUser, testDTO2);
                System.out.println(weekStepRepository.findAll().get(0).getStepCount());

                var testDTO3 = new StepDTO(testUser, 30,  startOfWeek.plusMinutes(88), startOfWeek.plusMinutes(98), startOfWeek.plusMinutes(108));
                stepService.addSingleStepForUser(testUser, testDTO3);
                System.out.println(weekStepRepository.findAll().size());
                System.out.println(weekStepRepository.findAll().get(0).getStepCount());

                // Expected step count
                var expectedStepCount = testDTO3.getStepCount();

                // Actual step count
                var actualStepCount = weekStepRepository.findByUserIdAndYear(testUser, testDTO3.getStartTime().getYear()).get(0).getStepCount();

                System.out.println(weekStepRepository.findAll().get(0).getStepCount());
                // Assert that the stepCount of the WeekStep has been updated correctly
                assertEquals(expectedStepCount, actualStepCount,
                        () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + weekStepRepository.findAll().size());
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
                var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);
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
                var result = weekStepRepository.findTopByUserIdOrderByIdDesc(testUser);
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
                    var storedStep = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);
                    var actualStepCount = storedStep.orElseThrow().getStepCount();
                    // TODO: Use new query
                    var storedWeekStep = weekStepRepository.findTopByUserIdOrderByIdDesc(testUser);
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
                    var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser).orElseThrow();
                    var actualUserId = result.getUserId();
                    var actualStepCount = result.getStepCount();
                    var actualStartTime = result.getStartTime();
                    var actualEndTime = result.getEndTime();
                    var actualUploadTime = result.getUploadTime();

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
                    var expectedYear = testDto.getStartTime().getYear();
                    var expectedWeek = DateHelper.getWeek(testDto.getEndTime());

                    // Actual values of the object stored in the database
                    var result = weekStepRepository.findTopByUserIdOrderByIdDesc(testUser)
                            .orElseThrow();
                    System.out.println(result.getWeek());
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
                var expectedYear = testDto.getStartTime().getYear();
                var expectedMonth = testDto.getEndTime().getMonthValue();

                // Actual values of the object stored in the database
                System.out.println(weekStepRepository.findAll().get(0));
                var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, testDto.getStartTime().getYear(), 1).orElseThrow();
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
            @DisplayName("Should throw 'DateTimeValueException' when list contains StepDTO with null startTime")
            public void testMultipleStepsForUser_ThrowsDateTimeValueException_WhenStartTimeIsNull() {
                // Create a StepDTO where startTime is null
                var badTestDto = testDTOBuilder.createStepDTOWhereStartTimeIsNull();
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "Start time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        DateTimeValueException.class, () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }

            @Test
            @DisplayName("Should throw 'DateTimeValueException' when StepDTO endTime is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenEndTimeIsNull() {
                // Create a StepDTO where endTim is null
                var badTestDto = testDTOBuilder.createStepDTOWhereEndTimeIsNull();
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "End time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        DateTimeValueException.class, () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }

            @Test
            @DisplayName("Should throw 'DateTimeValueException' when StepDTO uploadTime is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenUploadTimeIsNull() {
                // Create a StepDTO where uploadTime is null
                var badTestDto = testDTOBuilder.createStepDTOWhereUploadTimeIsNull();
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "Upload time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        DateTimeValueException.class, () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));
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
    @DisplayName("Test creation of WeekStepDTO")
    public class GetStepsPerDayForWeekTest {

        @Test
        @DisplayName("getStepsPerDayOfWeek should throw ValidationFailedException when user is null")
        public void testGetStepsPerDayOfWeekWithNullUser_ThrowsValidationFailedException() {
            // Act and assert: Call the method to be tested with null userId and assert that the correct exception is thrown
            assertThrows(ValidationFailedException.class, () -> stepService.getStepsPerDayForWeek(null));
        }

        @Test
        @DisplayName("getStepsPerDayOfWeek should throw ValidationFailedException with correct message when user is null")
        public void testGetStepsPerDayOfWeekWithNullUser() {
            // Act and assert: Call the method to be tested with null userId and assert that the exception contains the correct exception message
            ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> stepService.getStepsPerDayForWeek(null));
            assertEquals("User id and time must not be null", exception.getMessage());
        }


        @Test
        @DisplayName("getStepsPerDayOfWeek should return the correct total stepCount for the week")
        public void testGetTotalStepCountForWeek() {
            // Arrange: Create and save three test Step objects of the same week
            var firstTestStep = testStepBuilder.createStepOfFirstMinuteOfYear();
            stepRepository.save(firstTestStep);
            stepRepository.save(testStepBuilder.createStepOfSecondMinuteOfYear());
            stepRepository.save(testStepBuilder.createStepOfThirdMinuteOfYear());


            // Act: Call the method to be tested to receive the data from the test steps from the database
            var testWeekStepDTO = stepService.getStepsPerDayForWeek(testUser);

            // Expected values: The total stepCount of all three objects
            var expectedTotalStepCount = 10 + 20 + 30;

            // Actual values: Calculate the total stepCount for the week by adding up the stepCounts for each day
            var actualTotalStepCount = testWeekStepDTO.getMondayStepCount() +
                    testWeekStepDTO.getTuesdayStepCount() +
                    testWeekStepDTO.getWednesdayStepCount() +
                    testWeekStepDTO.getThursdayStepCount() +
                    testWeekStepDTO.getFridayStepCount() +
                    testWeekStepDTO.getSaturdayStepCount() +
                    testWeekStepDTO.getSundayStepCount();

            // Assert that the actual total step count matches the expected total step count
            assertEquals(expectedTotalStepCount, actualTotalStepCount);
        }

        @Test
        @DisplayName("getStepsPerDayOfWeek should return correct stepCount for a week that spans two different years")
        public void testGetStepsPerDayOfWeekForWeekSpanningTwoYears() {
            // Arrange: Set up two dates of different years but on same week
            var monday2022 = LocalDateTime.of(
                    2022, 12, 26, 1, 1).atZone(ZoneId.systemDefault()); // Monday december 2022
            var sunday2023 = LocalDateTime.of(
                    2023, 1, 1, 1, 1).atZone(ZoneId.systemDefault()); // Sunday januari 2023


            // Create Step objects of both years
            var step1 = new Step(testUser, 2022, monday2022, monday2022.plusMinutes(1), monday2022.plusMinutes(2));
            var step2 = new Step(testUser, 2022, monday2022.plusMinutes(3), monday2022.plusMinutes(4), monday2022.plusMinutes(5));
            var step3 = new Step(testUser, 2023, sunday2023, sunday2023.plusMinutes(1), sunday2023.plusMinutes(2));

            // Save Step objects to database
            stepRepository.saveAll(List.of(step1, step2, step3));

            // Act: Call method to be tested to receive the data from the database
            var testWeekStepDTO = stepService.getStepsPerDayForWeek(testUser);

            // Assert: Verify step counts for each day of the week
            assertEquals(4044, testWeekStepDTO.getMondayStepCount());
            assertEquals(0, testWeekStepDTO.getTuesdayStepCount());
            assertEquals(0, testWeekStepDTO.getWednesdayStepCount());
            assertEquals(0, testWeekStepDTO.getThursdayStepCount());
            assertEquals(0, testWeekStepDTO.getFridayStepCount());
            assertEquals(0, testWeekStepDTO.getSaturdayStepCount());
            assertEquals(2023, testWeekStepDTO.getSundayStepCount());
        }
    }

    @Test
    @DisplayName("Test Get Step Count Per Week For User")
    void testGetStepCountPerWeekForUser_ReturnsCorrectValues() {
        // Clean up existing data
        weekStepRepository.deleteAll();

        // Get current week and year
        var startOfWeek = DateHelper.getWeekStart(ZonedDateTime.now());

        // Create WeekStep objects for testing
        var stepsInCurrentWeek = new StepDTO(testUser, 1000, startOfWeek, startOfWeek.plusMinutes(1), startOfWeek.plusMinutes(2));
        var stepsInNextWeek = new StepDTO(testUser, 2000, startOfWeek.plusWeeks(1), startOfWeek.plusMinutes(1).plusWeeks(1), startOfWeek.plusMinutes(2).plusWeeks(1));

        stepService.addSingleStepForUser(testUser, stepsInCurrentWeek);
        stepService.addSingleStepForUser(testUser, stepsInNextWeek);


        // Invoke the method under test
        var weeklyStepCount = stepService.getStepCountPerWeekForUser(testUser, ZonedDateTime.now().getYear());

        // Retrieve expected values (adjust for negative array index)
        var expected1 = weeklyStepCount.getWeeklySteps().get(DateHelper.getWeek(stepsInCurrentWeek.getStartTime()));
        var expected2 = weeklyStepCount.getWeeklySteps().get(DateHelper.getWeek(stepsInNextWeek.getStartTime()));

        System.out.println(weeklyStepCount.getWeeklySteps());
        // Assertions
        assertNotNull(weeklyStepCount);
        assertEquals(testUser, weeklyStepCount.getUserId());
        assertEquals(53, weeklyStepCount.getWeeklySteps().size());
        assertEquals((Integer) 1000, expected1);
        assertEquals((Integer) 2000, expected2);
    }

    @Test
    @DisplayName("Test Get Step Count Per Week For User - No Step Data")
    void testGetStepCountPerWeekForUser_NoStepData_ReturnsEmptyList() {
        // Clean up existing data
        weekStepRepository.deleteAll();

        // Invoke the method under test
        var result = stepService.getStepCountPerWeekForUser(testUser, ZonedDateTime.now().getYear());

        // Assertions
        assertNotNull(result);
        assertEquals(testUser, result.getUserId());
        assertEquals(53, result.getWeeklySteps().size());
        assertTrue(result.getWeeklySteps().stream().allMatch(stepCount -> stepCount == 0));
    }

    @Test
    @DisplayName("Test Get Step Count Per Week For User - Week Out of Range")
    void testGetStepCountPerWeekForUser_WeekOutOfRange_IgnoresInvalidWeek() {
        // Clean up existing data
        weekStepRepository.deleteAll();

        var now = ZonedDateTime.now();
        var startOfWeek = DateHelper.getWeekStart(now);

        // Create WeekStep object with an invalid week
        var invalidWeek = new StepDTO(testUser, 500, startOfWeek.plusWeeks(53), startOfWeek.plusMinutes(1).plusWeeks(53), startOfWeek.plusMinutes(2).plusWeeks(53));
        stepService.addSingleStepForUser(testUser, invalidWeek);

        // Invoke the method under test
        var result = stepService.getStepCountPerWeekForUser(testUser, ZonedDateTime.now().getYear());

        // Assertions
        assertNotNull(result);
        assertEquals(testUser, result.getUserId());
        assertEquals(53, result.getWeeklySteps().size());
        assertTrue(result.getWeeklySteps().stream().allMatch(stepCount -> stepCount == 0));
    }

    @Test
    @DisplayName("Test Get Step Count Per Week For User - Multiple Steps in Different Weeks")
    void testGetStepCountPerWeekForUser_MultipleStepsInDifferentWeeks_ReturnsCorrectStepCounts() {
        // Clean up existing data
        weekStepRepository.deleteAll();

        var now = ZonedDateTime.now();
        var startOfWeek = DateHelper.getWeekStart(now);

        // Create WeekStep objects for testing
        var stepsInWeek1 = new StepDTO(testUser, 1000, startOfWeek, startOfWeek.plusMinutes(1), startOfWeek.plusMinutes(2));
        var stepsInWeek2 = new StepDTO(testUser, 2000, startOfWeek.plusWeeks(1), startOfWeek.plusMinutes(1).plusWeeks(1), startOfWeek.plusMinutes(2).plusWeeks(1));
        var stepsInWeek3 = new StepDTO(testUser, 1500, startOfWeek.plusWeeks(2), startOfWeek.plusMinutes(1).plusWeeks(2), startOfWeek.plusMinutes(2).plusWeeks(2));

        stepService.addSingleStepForUser(testUser, stepsInWeek1);
        stepService.addSingleStepForUser(testUser, stepsInWeek2);
        stepService.addSingleStepForUser(testUser, stepsInWeek3);

        // Invoke the method under test
        var result = stepService.getStepCountPerWeekForUser(testUser, ZonedDateTime.now().getYear());

        // Retrieve expected values
        var expected1 = result.getWeeklySteps().get(DateHelper.getWeek(stepsInWeek1.getStartTime()));
        var expected2 = result.getWeeklySteps().get(DateHelper.getWeek(stepsInWeek2.getStartTime()));
        var expected3 = result.getWeeklySteps().get(DateHelper.getWeek(stepsInWeek3.getStartTime()));
        System.out.println(result.getWeeklySteps());
        // Assertions
        assertNotNull(result);
        assertEquals(testUser, result.getUserId());
        assertEquals(53, result.getWeeklySteps().size());
        assertEquals((Integer) 1000, expected1);
        assertEquals((Integer) 2000, expected2);
        assertEquals((Integer) 1500, expected3);
    }

    @Test
    @DisplayName("Test Get Step Count Per Week For User - Multiple Steps in Same Week")
    void testGetStepCountPerWeekForUser_MultipleStepsInSameWeek_ReturnsSumOfStepCounts() {
        var now = ZonedDateTime.now();
        var startOfWeek = DateHelper.getWeekStart(now);

        // Create DTO objects for testing
        var stepsInWeek = new StepDTO(testUser, 500, startOfWeek, startOfWeek.plusMinutes(1), startOfWeek.plusMinutes(2));
        var additionalStepsInWeek = new StepDTO(testUser, 700, startOfWeek.plusDays(3), startOfWeek.plusMinutes(1).plusDays(3), startOfWeek.plusMinutes(2).plusDays(3));

        // Add data to test method
        stepService.addSingleStepForUser(testUser, stepsInWeek);
        stepService.addSingleStepForUser(testUser, additionalStepsInWeek);

        // Invoke the method under test
        var result = stepService.getStepCountPerWeekForUser(testUser, ZonedDateTime.now().getYear());
        var objectsInDataBase = weekStepRepository.findAll();

        // Retrieve expected value
        var expected = result.getWeeklySteps().get(DateHelper.getWeek(startOfWeek));

        // Assertions
        assertNotNull(result);
        assertEquals(testUser, result.getUserId());
        assertEquals(53, result.getWeeklySteps().size());
        assertEquals((Integer) 1200, expected); // Sum of step counts in the same week
        assertEquals(1, objectsInDataBase.size());
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
