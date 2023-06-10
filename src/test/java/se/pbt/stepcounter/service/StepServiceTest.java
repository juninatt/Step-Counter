package se.pbt.stepcounter.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.exception.DateTimeValueException;
import se.pbt.stepcounter.exception.InvalidUserIdException;
import se.pbt.stepcounter.exception.InvalidStepDataException;
import se.pbt.stepcounter.mapper.DateHelper;
import se.pbt.stepcounter.mapper.StepMapper;
import se.pbt.stepcounter.model.Step;
import se.pbt.stepcounter.repository.MonthStepRepository;
import se.pbt.stepcounter.repository.StepRepository;
import se.pbt.stepcounter.repository.WeekStepRepository;
import se.pbt.stepcounter.testobjects.TestObjectBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("StepService:")
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

    static TestObjectBuilder testObjectBuilder;

    @BeforeAll
    public static void init() {
        testObjectBuilder = new TestObjectBuilder(2023);
    }

    @AfterEach
    public void resetDataBase() {
        stepService.deleteStepTable();
        weekStepRepository.deleteAll();
        monthStepRepository.deleteAll();
    }

    @Nested
    @DisplayName("addSingleStepForUser():")
    public class AddSingleStepForUserTest {

        @Nested
        @DisplayName("Throws exception: ")
        public class AddSingleStepFurUserThrowsExceptionTest {

            @Test
            @DisplayName("Throws 'InvalidUserIdException' when input: userId, is null")
            public void testAddSingleStepForUser_ThrowsInvalidUserIdException_WhenUserIdIsNull() {
                // Create StepDTO where userId is null to serve as test data
                var testStepDTO = testObjectBuilder.getTestStepDTO();

                // Expected exception message
                var expectedMessage = "User ID cannot be null or empty";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(InvalidUserIdException.class,
                        () -> stepService.addSingleStepForUser(null, testStepDTO));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Throws 'InvalidUserIdException' when input: stepDTO, is null")
            public void testAddSingleStepForUser_ThrowsInvalidUserIdException_WhenStepDTOIsNull() {
                // Expected exception message
                var expectedMessage = "Object holding new data cant be null";

                // Assert that correct exception is thrown when StepDTO passed to method is null
                var result = assertThrows(InvalidStepDataException.class,
                        () -> stepService.addSingleStepForUser(testUser, null));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Throws 'DateTimeValueException' when input: stepDTO, has incompatible time-field values")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenTimeValueIsIncorrect() {
                // Create a StepDTO where time-fields are incompatible as test data
                var badTestDto = testObjectBuilder.getTestStepDTO();
                badTestDto.setEndTime(badTestDto.getStartTime().minusSeconds(1));

                // Expected exception message
                var expectedMessage = "Start time must be before end time";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(DateTimeValueException.class,
                        () -> stepService.addSingleStepForUser(testUser, badTestDto));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Trows 'DateTimeValueException' when input: stepDTO, field: startTime, is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenStartTimeIsNull() {
                // Create a StepDTO where startTime is null
                var badTestDto = testObjectBuilder.getTestStepDTO();
                badTestDto.setStartTime(null);

                // Expected exception message
                var expectedMessage = "Start time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(DateTimeValueException .class,
                        () -> stepService.addSingleStepForUser(testUser, badTestDto));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Trows 'DateTimeValueException' when input: stepDTO, field: endTime, is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenEndTimeIsNull() {
                // Create a StepDTO where endTime is null as test data
                var badTestDto = testObjectBuilder.getTestStepDTO();
                badTestDto.setEndTime(null);

                // Expected exception message
                var expectedMessage = "End time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(DateTimeValueException.class,
                        () -> stepService.addSingleStepForUser(testUser, badTestDto));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Throws 'DateTimeValueException' when input: stepDTO, field: uploadTime, is null")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenUploadTimeIsNull() {
                // Create a StepDTO where uploadTime is null as test data
                var badTestDto = testObjectBuilder.getTestStepDTO();
                badTestDto.setUploadTime(null);

                // Expected exception message
                var expectedMessage = "Upload time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(DateTimeValueException.class,
                        () -> stepService.addSingleStepForUser(testUser, badTestDto));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }
        }


        @Nested
        @DisplayName("Returns:")
        public class AddSingleStepReturnsTest {

            Step testStep;


            @BeforeEach
            void setup() {
                // Create and save the created test Step object to all tables in database
                testStep = testObjectBuilder.getTestStep();
                saveToAllTables(StepMapper.mapper.stepToStepDTO(testStep));
            }

            @Test
            @DisplayName("Returns object of class 'Step'")
            public void testAddSingleStepForUser_ReturnsObjectOfClassStep() {

                // Create a StepDTO to serve as test data
                var testDto = testObjectBuilder.getTestStepDTO();

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
            @DisplayName("Returns Step with correct field values when Step in database is updated")
            public void testAddSingleStepForUser_ReturnsStepWithCorrectUserId_WhenUpdatingStep() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testObjectBuilder.getTestStepDTO();

                // Pass the test data to the method to be tested
                var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected field values of the returned object
                var expectedUserId = testUser;
                var expectedStepCount = testStepDTO.getStepCount();
                var expectedStartTime = testStep.getStartTime();
                var expectedEndTime = testStepDTO.getEndTime();
                var expectedUploadTime = testStepDTO.getUploadTime();

                // Assert that the returned Step has the expected field values
                assertAll("Step fields",
                        () -> assertEquals(expectedUserId, result.getUserId(), "Incorrect userId value."),
                        () -> assertEquals(expectedStepCount, result.getStepCount(), "Incorrect stepCount value."),
                        () -> assertEquals(expectedStartTime, result.getStartTime(), "Incorrect startTime value."),
                        () -> assertEquals(expectedEndTime, result.getEndTime(), "Incorrect endTime value."),
                        () -> assertEquals(expectedUploadTime, result.getUploadTime(), "Incorrect uploadTime value.")
                );
            }

            @Test
            @DisplayName("Returns Step with correct field values when new Step object is created")
            public void testAddSingleStepForUser_ReturnsStepWithCorrectUserId_WhenCreatingNewStep() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testObjectBuilder.getTestStepDTO();

                // Pass the test data to the method to be tested
                var result = stepService.addSingleStepForUser(testUser, testStepDTO);

                // Expected field values of the returned object
                var expectedUserId = testUser;
                var expectedStepCount = testStepDTO.getStepCount();
                var expectedStartTime = testStepDTO.getStartTime();
                var expectedEndTime = testStepDTO.getEndTime();
                var expectedUploadTime = testStepDTO.getUploadTime();

                // Assert that the returned Step has the expected field values
                assertAll("Step fields",
                        () -> assertEquals(expectedUserId, result.getUserId(), "Incorrect userId value."),
                        () -> assertEquals(expectedStepCount, result.getStepCount(), "Incorrect stepCount value."),
                        () -> assertEquals(expectedStartTime, result.getStartTime(), "Incorrect startTime value."),
                        () -> assertEquals(expectedEndTime, result.getEndTime(), "Incorrect endTime value."),
                        () -> assertEquals(expectedUploadTime, result.getUploadTime(), "Incorrect uploadTime value.")
                );
            }
        }


        @Nested
        @DisplayName("Updates database:")
        public class AddSingleStepShouldUpdateStepObjectsCorrectly {

            // Create a test Step object
            Step testStep;

            @BeforeEach
            void setup() {
                testStep = testObjectBuilder.getTestStep();
                // Save the created test Step object to all tables in database
                saveToAllTables(StepMapper.mapper.stepToStepDTO(testStep));
            }

            @Test
            @DisplayName("Creates new Step object if startTime of StepDTO is before endTime of Step")
            public void testAddSingleStepForUser_DoesNotCreateNewStep_WhenStartTimeIsBeforeEndTime() {
                // Create a StepDTO to serve as test data that starts before the database step ends
                var testStepDTO = testObjectBuilder.getTestStepDTO();

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
            @DisplayName("Updates stepCount of Step in database if input is valid and new data starts before old Step ends")
            public void testAddSingleStepForUser_UpdatesStepCountOfStep_IfStepIsFoundInDataBase() {
                // Create a StepDTO to serve as test data
                var testDTO = testObjectBuilder.getTestStepDTO();

                // Call the method to test and retrieve the data from the database
                stepService.addSingleStepForUser(testUser, testDTO);
                var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);

                // Expected stepCount after Step is updated
                var expectedStepCount = testDTO.getStepCount();

                // Actual object in database and stepCount
                var actualStepCount = result.orElseThrow().getStepCount();

                // Assert that stepCount of Step has been updated correctly
                assertEquals(expectedStepCount, actualStepCount,
                        "Expected stepCount to be '" + expectedStepCount + "'  but was '" + actualStepCount + "'. " + result);
            }

            @Test
            @DisplayName("Updates endTime of Step in database if input is valid and new data starts before old Step ends")
            public void testAddSingleStepForUser_UpdatesEndTimeOfStep_IfStepIsFoundInDataBase() {
                // Create a StepDTO to serve as test data
                var testStepDTO = testObjectBuilder.getTestStepDTO();

                // Call the method to test and fetch the data from the database
                stepService.addSingleStepForUser(testUser, testStepDTO);
                var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);

                // Expected endTime after Step is updated
                var expectedEndTime = testStepDTO.getEndTime();

                // Actual object in database and endTime
                var actualEndTime = result.orElseThrow().getEndTime();

                // Assert that endTime of Step has been updated correctly
                assertEquals(expectedEndTime, actualEndTime,
                        "Expected endTime to be '" + expectedEndTime + "'  but was '" + actualEndTime + "'. " + result);
            }

            @Test
            @DisplayName("Updates  uploadTime of Step in database if input is valid and new data starts before old Step ends")
            public void testAddSingleStepForUser_UpdatesUploadTimeOfStep_IfStepIsFoundInDataBase() {
                // Create a StepDTO to serve as test data
                var testDTO = testObjectBuilder.getTestStepDTO();

                // Call the method to test and fetch the added data from the database
                stepService.addSingleStepForUser(testUser, testDTO);
                var result = stepRepository.findFirstByUserIdOrderByStartTimeDesc(testUser);

                // Expected uploadTime after Step is updated
                var expectedUploadTime = testDTO.getUploadTime();

                // Actual object in database and uploadTime
                var actualUploadTime = result.orElseThrow().getUploadTime();

                // Assert that uploadTime of Step has been updated correctly
                assertEquals(expectedUploadTime, actualUploadTime,
                        "Expected uploadTime to be '" + expectedUploadTime + "'  but was '" + actualUploadTime + "'. " + result);
            }
        }

        @Nested
        @DisplayName("Updates WeekStep in database")
        public class AddSingleStepShouldUpdateWeekStepObjectsCorrectly {

            // Create a test Step object
            StepDTO testDTO = testObjectBuilder.getTestStepDTO();

            @BeforeEach
            void setup() {
                // Save the created test Step object to all tables in database
                saveToAllTables(testDTO);
            }

            @Test
            @DisplayName("Updates stepCount of WeekStep object if input is valid")
            public void   testAddingSingleStepForUser_UpdatesStepCountInWeekStepTable_IfDataExistsInDataBase() {
                // Create and save step to database
                var dto1 = testObjectBuilder.getTestStepDTO();
                var dto2 = testObjectBuilder.copyAndPostponeMinutes(dto1, 5);
                dto2.setStepCount(69);

                // Pass the test data to the method to be tested
                stepService.addSingleStepForUser(testUser, dto1);
                stepService.addSingleStepForUser(testUser, dto2);

                // Call the method to test
                var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, 2023, 1);

                // Expected stepCount of WeekStep object in database
                var expectedStepCount = dto2.getStepCount();

                // Actual object in database and stepCount
                var actualStepCount = result.orElseThrow().getStepCount();

                // Assert that the stepCount of the WeekStep has been updated correctly
                assertEquals(expectedStepCount, actualStepCount,
                        () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + result);
            }

            @Test
            @DisplayName("Updates stepCount value in database when new DTO is added")
            public void testAddingSingleStepForUser_CreatesOneWeekStepObject_IfStepObjectsAreSameDay() {
                // Create DTOs to add and call test method efter each
                var dto1 = testObjectBuilder.getTestStepDTO();
                var dto2 = testObjectBuilder.copyAndPostponeMinutes(dto1, 120);
                var dto3 = testObjectBuilder.copyAndPostponeMinutes(dto2, 120);

                stepService.addSingleStepForUser(testUser, dto1);
                stepService.addSingleStepForUser(testUser, dto2);
                stepService.addSingleStepForUser(testUser, dto3);

                // Expected step count
                var expectedStepCount = dto3.getStepCount();

                // Actual step count
                var actualStepCount = weekStepRepository.findByUserIdAndYear(testUser, dto3.getStartTime().getYear()).get(0).getStepCount();

                // Assert that the stepCount of the WeekStep has been updated correctly
                assertEquals(expectedStepCount, actualStepCount,
                        () -> "Expected stepCount to be '" + expectedStepCount + "' but was '" + actualStepCount + "'. " + weekStepRepository.findAll().size());
            }
        }

        @Nested
        @DisplayName("Adds to database:")
        public class AddSingleStepShouldCreateNewObjects {

                @Test
                @DisplayName("Creates new Step if no Step is found in database")
                public void testAddSingleStepForUser_CreatesNewStep_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testObjectBuilder.getTestStepDTO();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Assert that Step table is not empty
                    assertFalse(stepRepository.findAll().isEmpty());
                }

                @Test
                @DisplayName("Creates new WeekStep if no Step is found in database")
                public void testAddSingleStepForUser_CreatesNewWeekStep_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testObjectBuilder.getTestStepDTO();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Assert that WeekStep table is not empty
                    assertFalse(weekStepRepository.findAll().isEmpty());
                }

                @Test
                @DisplayName("Creates new MonthStep if no Step is found in database")
                public void testAddSingleStepForUser_CreatesNewMonthStep_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testObjectBuilder.getTestStepDTO();

                    // Pass the test data to the method to be tested
                    stepService.addSingleStepForUser(testUser, testStepDTO);

                    // Assert that MonthStep is not empty
                    assertFalse(monthStepRepository.findAll().isEmpty());
                }

            @Test
            @DisplayName("Creates new Step with correct stepCount if no Step is found in database")
            public void testAddSingleStepForUser_CreatesStepWithCorrectStepCount_IfNoStepIsFoundInDataBase() {
                // Make sure database is empty and create a StepDTO to serve as test data
                assertTrue(dataBaseIsEmpty());
                var testStepDTO = testObjectBuilder.getTestStepDTO();

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
            @DisplayName("Creates new WeekStep with correct stepCount if no Step is found in database")
            public void testAddSingleStepForUser_CreatesWeekStepWithCorrectStepCount_IfNoStepIsFoundInDataBase() {
                // Make sure database is empty and create a StepDTO to serve as test data
                assertTrue(dataBaseIsEmpty());
                var testStepDTO = testObjectBuilder.getTestStepDTO();

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
                @DisplayName("Creates new MonthStep with correct stepCount if no Step is found in database")
                public void testAddSingleStepForUser_CreatesMonthStepWithCorrectStepCount_IfNoStepIsFoundInDataBase() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testObjectBuilder.getTestStepDTO();

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
                @DisplayName("Creates objects for all tables with equal stepCount when no step exists for user in database")
                public void testAddingSingleStepForUser_AddsStepCountToAllTables()  {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testStepDTO = testObjectBuilder.getTestStepDTO();

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
                @DisplayName("Persists all fields of the Step object to the database correctly")
                public void testAddSingleStepForUser_AddsAllFieldsCorrectlyTStepTable() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testDto = testObjectBuilder.getTestStepDTO();

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
                @DisplayName("Persists all WeekStep-fields in database correctly when database is empty")
                public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToWeekStepTable() {
                    // Make sure database is empty and create a StepDTO to serve as test data
                    assertTrue(dataBaseIsEmpty());
                    var testDto = testObjectBuilder.getTestStepDTO();

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
            @DisplayName("Persists all MonthStep-fields in database correctly when database is empty")
            public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToMonthStepTable() {
                // Make sure database is empty and create a StepDTO to serve as test data
                assertTrue(dataBaseIsEmpty());
                var testDto = testObjectBuilder.getTestStepDTO();

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
    @DisplayName("addMultipleStepsForUser():")
    public class AddMultipleStepsForUserTest {

        // Create a list to hold the test data
        List<StepDTO> testStepDTOList = new ArrayList<>();



        @BeforeEach
        public void setUp() {

            var dto1 = testObjectBuilder.getTestStepDTO();
            var dto2 = testObjectBuilder.copyAndPostponeMinutes(dto1, 1);
            var dto3 = testObjectBuilder.copyAndPostponeMinutes(dto2, 1);

            // Create and add StepDTO:s to the test list
            testStepDTOList.add(dto1);
            testStepDTOList.add(dto2);
            testStepDTOList.add(dto3);
        }

        @Nested
        @DisplayName("Throws:")
        public class AddMultipleStepsShouldThrowException {

            @Test
            @DisplayName("Throws 'InvalidUserIdException'' when input: userId, is null")
            public void testAddMultipleStepsForUser_ThrowsInvalidUserIdException_WhenUserIdInputIsNull() {
                // Expected exception message
                var expectedMessage = "User ID cannot be null or empty";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(
                        InvalidUserIdException.class, () -> stepService.addMultipleStepsForUser(null, testStepDTOList));
                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Throws 'InvalidUserIdException'' when input: dtoList, is null")
            public void  testAddMultipleStepsForUser_ThrowsInvalidUserIdException_WhenListIsNull() {
                // Expected exception message
                var expectedMessage = "User ID cannot be null or empty";

                // Assert that correct exception is thrown when the list passed to the method is null
                var result = assertThrows(InvalidUserIdException.class,
                        () -> stepService.addMultipleStepsForUser(testUser, null));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
            }

            @Test
            @DisplayName("Throws 'DateTimeValueException' when StepDTO with incompatible time fields is encountered")
            public void testAddMultipleStepsForUser_ThrowsDateTimeValueException_WhenListContainsBadTimeFields() {
                // Create StepDTO with incompatible time fields and add to test list
                var badTimeFieldsDTO = testObjectBuilder.getTestStepDTO();
                badTimeFieldsDTO.setEndTime(badTimeFieldsDTO.getStartTime().minusSeconds(1));
                testStepDTOList.add(badTimeFieldsDTO);

                // Expected exception message
                var expectedMessage = "Start time must be before end time";

                // Assert that correct exception is thrown when the list passed to the method is null
                var result = assertThrows(DateTimeValueException.class,
                        () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '"+ result.getMessage() + "'");
                testStepDTOList.remove(badTimeFieldsDTO);
            }

            @Test
            @DisplayName("Throws 'DateTimeValueException' when StepDTO with null startTime is encountered")
            public void testMultipleStepsForUser_ThrowsDateTimeValueException_WhenStartTimeIsNull() {
                // Create a StepDTO where startTime is null
                var badTestDto = testObjectBuilder.getTestStepDTO();
                badTestDto.setStartTime(null);
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "Start time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(DateTimeValueException.class,
                        () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }

            @Test
            @DisplayName("Throws 'DateTimeValueException' when StepDTO with null endTime is encountered")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenEndTimeIsNull() {
                // Create a StepDTO where endTim is null
                var badTestDto = testObjectBuilder.getTestStepDTO();
                badTestDto.setEndTime(null);
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "End time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(DateTimeValueException.class,
                        () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }

            @Test
            @DisplayName("Throws 'DateTimeValueException' when StepDTO with null uploadTime is encountered")
            public void testAddSingleStepForUser_ThrowsDateTimeValueException_WhenUploadTimeIsNull() {
                // Create a StepDTO where uploadTime is null
                var badTestDto = testObjectBuilder.getTestStepDTO();
                badTestDto.setUploadTime(null);
                testStepDTOList.add(badTestDto);

                // Expected exception message
                var expectedMessage = "Upload time cant be null";

                // Assert that correct exception is thrown when test data is passed to method
                var result = assertThrows(DateTimeValueException.class,
                        () -> stepService.addMultipleStepsForUser(testUser, testStepDTOList));

                assertTrue(result.getMessage().contains(expectedMessage),
                        "Expected exception message to be '" + expectedMessage + "' but was '" + result.getMessage() + "'");
                testStepDTOList.remove(badTestDto);
            }
        }

        @Nested
        @DisplayName("Returns:")
        public class AddMultipleStepsShouldReturnCorrectValues {

            @Test
            @DisplayName("Returns a Step object with the total stepCount of the objects in list passed as input")
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
    @DisplayName("getStepCountPerWeekForUser():")
    public class GetStepCountPerWeekForUserTest {

        @Test
        @DisplayName("Returns fields of 0 if no data exists for user")
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
        @DisplayName("Returns step count 0 when week of step is out of bounds")
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
        @DisplayName("Returns correct step count and length different weeks")
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
        @DisplayName("Returns correct step count same week")
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

        @Test
        @DisplayName("Returns correct step count different weeks")
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

            // Assertions
            assertNotNull(weeklyStepCount);
            assertEquals(testUser, weeklyStepCount.getUserId());
            assertEquals(53, weeklyStepCount.getWeeklySteps().size());
            assertEquals((Integer) 1000, expected1);
            assertEquals((Integer) 2000, expected2);
        }
    }


    @Nested
    @DisplayName("getStepsPerDayForWeek():")
    public class GetStepsPerDayForWeekTest {

        @Test
        @DisplayName("Throws  ValidationFailedException when user is null")
        public void testGetStepsPerDayOfWeekWithNullUser_ThrowsValidationFailedException() {
            // Act and assert: Call the method to be tested with null userId and assert that the correct exception is thrown
            assertThrows(InvalidStepDataException.class, () -> stepService.getStepsPerDayForWeek(null));
        }

        @Test
        @DisplayName("Throws ValidationFailedException with correct message when user is null")
        public void testGetStepsPerDayOfWeekWithNullUser() {
            // Act and assert: Call the method to be tested with null userId and assert that the exception contains the correct exception message
            InvalidStepDataException exception = assertThrows(InvalidStepDataException.class, () -> stepService.getStepsPerDayForWeek(null));
            assertEquals("User id and time must not be null", exception.getMessage());
        }


        @Test
        @DisplayName("Returns the correct total stepCount for the week")
        public void testGetTotalStepCountForWeek() {
            // Arrange: Create and save three test Step objects of the same week
            var firstTestStep = testObjectBuilder.getTestStep();
            stepRepository.save(firstTestStep);
            stepRepository.save(testObjectBuilder.copyAndPostponeMinutes(firstTestStep, 1));
            stepRepository.save(testObjectBuilder.copyAndPostponeMinutes(firstTestStep, 2));


            // Act: Call the method to be tested to receive the data from the test steps from the database
            var testWeekStepDTO = stepService.getStepsPerDayForWeek(testUser);

            // Expected values: The total stepCount of all three objects
            var expectedTotalStepCount = firstTestStep.getStepCount() * 3;

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
        @DisplayName("Returns correct stepCount for a week that spans two different years")
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


    @Nested
    @DisplayName("Parameterized tests: ")
    public class ParameterizedTests {

        @DisplayName("Add Single Step For User Validation Tests")
        @ParameterizedTest(name = "When adding a step with userId={0}, ValidationFailedException is thrown: {1}")
        @CsvSource({
                "'', true",
                "1, false",
        })
        public void testAddSingleStepForUser_ThrowsUserIdValueException(String userId, boolean shouldThrow) {
            var testStepDTO = testObjectBuilder.getTestStepDTO();
            if (shouldThrow) {
                assertThrows(InvalidUserIdException.class, () -> stepService.addSingleStepForUser(userId.isEmpty() ? null : userId, testStepDTO));
            } else {
                assertDoesNotThrow(() -> stepService.addSingleStepForUser(userId.isEmpty() ? null : userId, testStepDTO));
            }
        }
    }

    private void saveToAllTables(StepDTO dto) {
        stepRepository.save(StepMapper.mapper.stepDtoToStep(dto));
        var weekStep  = StepMapper.mapper.stepDtoToWeekStep(dto);
        weekStepRepository.save(weekStep);
        var monthStep  = StepMapper.mapper.stepDtoToMonthStep(dto);
        monthStepRepository.save(monthStep);
    }
    private boolean dataBaseIsEmpty() {
        return stepRepository.findAll().isEmpty()
                && weekStepRepository.findAll().isEmpty()
                && monthStepRepository.findAll().isEmpty();
    }
}
