package se.sigma.boostapp.boost_app_java.service.stepservicelogic;

import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.mapper.DateHelper;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;
import se.sigma.boostapp.boost_app_java.service.StepService;
import se.sigma.boostapp.boost_app_java.util.StepDtoSorter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    LocalDateTime now = LocalDateTime.now();

    String testUser = "testUser";

    StepDtoSorter sorter;

    @Before
    public void setUp() {
        sorter = new StepDtoSorter();
    }


    @AfterEach
    public void cleanUp() {
        stepService.deleteStepTable();
        weekStepRepository.deleteAll();
        monthStepRepository.deleteAll();
    }

    @Nested
    @DisplayName("addSingleStepForUser method:")
    public class AddSingleStepForUserTest {


        @Test
        @DisplayName("Should return 'Invalid Data' object when userId-input is null")
        public void testAddSingleStepForUser_ReturnsEmptyOptional_WhenUserIdIsNull() {
            // Arrange
            var testDto = new StepDTO(13, now, now.minusMinutes(1), now.minusMinutes(2));

            // Act
            var result = stepService.addSingleStepForUser(null, testDto).get();

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedStepCount = 0;
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualStepCount = result.getStepCount();
            var actualUploadTime = result.getUploadedTime();

            // Assert
            assertEquals(expectedUserId, actualUserId, () -> "Expected userId to be '" + expectedUserId + "' but was " + actualUserId);
            assertEquals(expectedUserId, actualUserId, () -> "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount);
            assertEquals(expectedUserId, actualUserId, () -> "Expected uploadTime to be '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when stepDTO-input is null")
        public void testAddSingleStepForUser_ReturnsEmptyOptional_WhenTestDtoIsNull() {
            // Arrange
            var testDto = new StepDTO(13, now, now.minusMinutes(1), now.minusMinutes(2));

            // Act
            var result = stepService.addSingleStepForUser(testUser, null).get();

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedStepCount = 0;
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualStepCount = result.getStepCount();
            var actualUploadTime = result.getUploadedTime();

            // Assert
            assertEquals(expectedUserId, actualUserId, () -> "Expected userId to be '" + expectedUserId + "' but was " + actualUserId);
            assertEquals(expectedUserId, actualUserId, () -> "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount);
            assertEquals(expectedUserId, actualUserId, () -> "Expected uploadTime to be '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return object of Step class when Step is used as input")
        public void testAddSingleStepForUser_ReturnsObjectOfStepClass() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));

            // Act
            var actual = stepService.addSingleStepForUser(testUser, testDto).orElse(null);

            // Expected values
            var expected = Step.class;

            // Assert
            assertNotNull(actual);
            assertEquals(expected, actual.getClass(), () -> "Expected: '" + expected + "' but got: " + actual.getClass());
        }

        @Test
        @DisplayName("Should create new Step if no Step is found in database")
        public void testAddSingleStepForUser_CreatesNewStep_IfNoStepIsFoundInDataBase() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            int expected = 13;

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expected, result.get().getStepCount(), "Expected step count to be '" + expected + "' but was " + result.get().getStepCount());
        }

        @Test
        @DisplayName("Should update stepCount of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepCount_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(new Step("testUser", 56, now, now.plusMinutes(1), now.plusMinutes(2)));
            var testDto = new StepDTO(13, now.plusMinutes(3), now.plusMinutes(4), now.plusMinutes(5));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expected = 56 + 13;

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expected, result.get().getStepCount(), "Expected step count to be '" + expected + "' but was " + result.get().getStepCount());
        }

        @Test
        @DisplayName("Should update Step endTime of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepEndTime_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(new Step("testUser", 56, now, now.plusMinutes(1), now.plusMinutes(2)));
            var testDto = new StepDTO(13, now.plusMinutes(3), now.plusMinutes(4), now.plusMinutes(5));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expected = testDto.getEndTime();

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expected, result.get().getEndTime(), "Expected end time to be '" + expected + "' but was " + result.get().getEndTime());
        }

        @Test
        @DisplayName("Should update Step uploadTime of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepUploadTime_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(new Step("testUser", 56, now, now.plusMinutes(1), now.plusMinutes(2)));
            var testDto = new StepDTO(13, now.plusMinutes(3), now.plusMinutes(4), now.plusMinutes(5));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expected = testDto.getUploadTime();

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expected, result.get().getUploadedTime(), "Expected step upload time to be '" + expected + "' but was " + result.get().getUploadedTime());
        }

        @Test
        @DisplayName("Should return object with correct userID when creating new Step")
        public void testAddSingleStepForUser_ReturnsObjectWithCorrectValues_WhenCreatingNewStep() {
            // Arrange
            var mockDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));

            // Act
            var result = stepService.addSingleStepForUser(testUser, mockDto);

            // Expected values
            var expected = testUser;

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expected, result.get().getUserId(), "Expected userId to be '" + expected + "'  but was " + result.get().getUserId());
        }

        @Test
        @DisplayName("Should return object with correct userId when updating current Step")
        public void testAddSingleStepForUser_ReturnsObjectWithUpdatedValues_WhenUpdatingStep() {
            // Arrange
            stepRepository.save(new Step("testUser", 56, now, now.plusMinutes(1), now.plusMinutes(2)));
            var testDto = new StepDTO(13, now.plusMinutes(3), now.plusMinutes(4), now.plusMinutes(5));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expected = testUser;

            // Assert
            assertTrue(result.isPresent());
            assertEquals(expected, result.get().getUserId(), "Expected userId to be '" + expected + "' but was " + result.get().getUserId());
        }

        @Test
        @DisplayName("Should add Step stepCount to WeekStep-table")
        public void testAddSingleStepForUser_AddsStepCountToWeekStepTable() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = weekStepRepository.getStepCountByUserIdYearAndWeek(testUser, now.getYear(), DateHelper.getWeek(now));

            // Expected values
            var expected = 13;

            // Assert
            assertTrue(result.isPresent(), "Expected step count to be returned but it was empty");
            assertEquals(Optional.of(expected), result, "Expected step count to be '" + expected + "'  but got " + result.get());
        }

        @Test
        @DisplayName("Should store all WeekStep-fields in database correctly")
        public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToWeekStepTable() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, now.getYear(), DateHelper.getWeek(now));

            // Expected values
            var expectedUserId = testUser;
            var expectedYear = testDto.getYear();
            var expectedWeek = DateHelper.getWeek(testDto.getEndTime());
            var expectedStepCount = testDto.getStepCount();

            // Actual values
            var actualUserId = result.get().getUserId();
            var actualYear = result.get().getYear();
            var actualWeek = result.get().getWeek();
            var actualStepCount = result.get().getSteps();

            // Assert
            assertTrue(result.isPresent(), "Expected WeekStep to be returned but it was empty");
            assertAll(
                    () -> assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but got " + actualUserId),
                    () -> assertEquals(expectedYear, actualYear, "Expected year to be '" + expectedYear + "' but got " + actualYear),
                    () -> assertEquals(expectedWeek, actualWeek, "Expected week to be '" + expectedWeek + "' but got " + actualWeek),
                    () -> assertEquals(expectedStepCount, actualStepCount, "Expected steps to be '" + expectedStepCount + "' but got " + actualStepCount)
            );
        }

        @Test
        @DisplayName("Should add Step stepCount to MonthStep-table")
        public void testAddSingleStepForUser_AddsStepCountToMonthStepTable() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = monthStepRepository.findByUserIdAndYearAndMonth(testUser, now.getYear(), now.getMonthValue());

            // Expected values
            var expected = 13;

            // Assert
            assertTrue(result.isPresent(), "Expected step count to be returned but it was empty");
            assertEquals(expected, result.get().getSteps(), "Expected step count to be '" + expected + "' but got " + result.get());
        }

        @Test
        @DisplayName("Should store all MonthStep-fields in database correctly")
        public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToMonthStepTable() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = monthStepRepository.findByUserIdAndYearAndMonth(testUser, now.getYear(), now.getMonthValue());

            // Expected values
            var expectedUserId = testUser;
            var expectedYear = testDto.getYear();
            var expectedMonth = testDto.getEndTime().getMonthValue();
            var expectedStepCount = testDto.getStepCount();

            // Actual values
            var actualUserId = result.get().getUserId();
            var actualYear = result.get().getYear();
            var actualMonth = result.get().getMonth();
            var actualStepCount = result.get().getSteps();

            // Assert
            assertTrue(result.isPresent(), "Expected MonthStep to be returned but it was empty");
            assertAll(
                    () -> assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but got " + actualUserId),
                    () -> assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be '" + expectedStepCount + "' but got" + actualStepCount),
                    () -> assertEquals(expectedYear, actualYear, "Expected year to be '" + expectedYear + "' but got" + actualYear),
                    () -> assertEquals(expectedMonth, actualMonth, "Expected month to be '" + expectedMonth + "' but got" + actualMonth)
            );
        }

        @Test
        @DisplayName("Should create new Step when users latest step in database is (2 weeks)old")
        public void testAddSingleStepForUser_CreatesNewStep_WhenPreviousStepIs2WeeksOld() {
            // Arrange
            var testDto = new StepDTO(13, now.minusDays(3), now.minusDays(2), now.minusDays(1));
            var oldStep = new Step(testUser, 13, now.minusDays(15), now.minusDays(14), now.minusDays(13));
            stepRepository.save(oldStep);
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = stepService.getLatestStepFromUser(testUser);

            // Expected values
            var expectedUserId = testUser;
            var expectedStartTime = testDto.getStartTime().getSecond();
            var expectedEndTime = testDto.getEndTime().getSecond();

            // Actual values
            var actualUserId = result.get().getUserId();
            var actualStartTime = result.get().getStartTime().getSecond();
            var actualEndTime = result.get().getStartTime().getSecond();

            // Assert
            assertAll(
                    () -> assertTrue(result.isPresent(), "Expected a step to be returned but it was empty"),
                    () -> assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but got " + actualUserId),
                    () -> assertEquals(expectedStartTime, actualStartTime, "Expected start time to be '" + expectedStartTime + "' but got " + actualStartTime),
                    () -> assertEquals(expectedEndTime, actualEndTime, "Expected end time to be '" + expectedEndTime + "' but got " + actualEndTime)
            );
        }

        @Test
        @DisplayName("Should create new Step when users latest step in database is (1 weeks)old")
        public void testAddSingleStepForUser_CreatesNewStep_WhenPreviousStepIs1WeekOld() {
            // Arrange
            var testDto = new StepDTO(13, now.minusDays(3), now.minusDays(2), now.minusDays(1));
            var oldStep = new Step(testUser, 13, now.minusDays(9), now.minusDays(8), now.minusDays(7));
            stepRepository.save(oldStep);
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = stepService.getLatestStepFromUser(testUser);

            // Expected values
            var expectedUserId = testUser;
            var expectedStartTime = testDto.getStartTime().getSecond();
            var expectedEndTime = testDto.getEndTime().getSecond();

            // Actual values
            var actualUserId = result.get().getUserId();
            var actualStartTime = result.get().getStartTime().getSecond();
            var actualEndTime = result.get().getEndTime().getSecond();

            // Assert
            assertAll(
                    () -> assertTrue(result.isPresent(), "Expected a step to be returned but it was empty"),
                    () -> assertEquals(expectedUserId, actualUserId, "Expected userId to be " + expectedUserId + " but was " + actualUserId),
                    () -> assertEquals(expectedStartTime, actualStartTime, "Expected start time to be " + expectedStartTime + " but was " + actualStartTime),
                    () -> assertEquals(expectedEndTime, actualEndTime, "Expected end time to be " + expectedEndTime + " but was " + actualEndTime)
            );
        }

        @Test
        @DisplayName("Should return Step with correctly updated values after first step is added")
        public void testAddSingleStepForUser_UpdatesStepCorrectly() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));
            var additionalTestDto = new StepDTO(13, now.plusMinutes(3), now.plusMinutes(4), now.plusMinutes(5));

            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var savedStep = stepService.addSingleStepForUser(testUser, additionalTestDto);

            // Expected values
            var expectedUserId = testUser;
            var expectedStartTime = additionalTestDto.getStartTime().getSecond();
            var expectedEndTime = additionalTestDto.getEndTime().getSecond();

            // Actual values
            var actualUserId = savedStep.get().getUserId();
            var actualStartTime = savedStep.get().getStartTime().getSecond();
            var actualEndTime = savedStep.get().getEndTime().getSecond();

            // Assert
            assertAll(
                    () -> assertEquals(expectedUserId, actualUserId, "Expected user id to be " + expectedUserId + " but was " + actualUserId),
                    () -> assertEquals(expectedStartTime, actualStartTime, "Expected start time to be " + expectedStartTime + " but was " + actualStartTime),
                    () -> assertEquals(expectedEndTime, actualEndTime, "Expected end time to be " + expectedEndTime + " but was " + actualEndTime)
            );
        }

        @Test
        @DisplayName("Returns 'Invalid Data' object when DTO with invalid time fields is used as input")
        public void testAddSingleStepForUser_ReturnsOptionalEmpty_WhenTimeValueIsIncorrect() {
            // Arrange
            var testDto = new StepDTO(13, now, now.minusMinutes(1), now.minusMinutes(2));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto).get();

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedStepCount = 0;
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualStepCount = result.getStepCount();
            var actualUploadTime = result.getUploadedTime();

            // Assert
            assertEquals(expectedUserId, actualUserId, () -> "Expected userId to be '" + expectedUserId + "' but was " + actualUserId);
            assertEquals(expectedUserId, actualUserId, () -> "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount);
            assertEquals(expectedUserId, actualUserId, () -> "Expected uploadTime to be '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should add stepCount to all tables when no step exists for user in database")
        public void testAddingSingleStepForUser_AddsStepCountToAllTables() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));
            stepService.addSingleStepForUser(testUser, testDto);

            // Expected Values
            var expected = 13;

            // Actual values
            var actualSingleStepCount = stepRepository.getStepCountByUserIdAndDateRange(testUser, now, now.plusMinutes(3));
            var actualWeekStepCount = weekStepRepository.getStepCountByUserIdYearAndWeek(testUser, now.getYear(), DateHelper.getWeek(now));
            var actualMonthStepCount = monthStepRepository.getStepCountByUserIdYearAndMonth(testUser, now.getYear(), now.getMonthValue());

            // Assert
            assertAll(
                    () -> assertTrue(actualSingleStepCount.isPresent()),
                    () -> assertTrue(actualWeekStepCount.isPresent()),
                    () -> assertTrue(actualMonthStepCount.isPresent()),
                    () -> assertEquals(Optional.of(expected), actualSingleStepCount),
                    () -> assertEquals(Optional.of(expected), actualWeekStepCount),
                    () -> assertEquals(Optional.of(expected), actualMonthStepCount)
            );
        }

        @Test
        @DisplayName("Should update stepCount of all tables when step exists for user in database")
        public void testAddingSingleStepForUser_UpdatesStepCountOfAllTables() {
            // Arrange
            var existingStep = new Step(testUser, 56, now, now.plusMinutes(1), now.plusMinutes(2));
            var existingWeekStep = new WeekStep(testUser, DateHelper.getWeek(now), now.getYear(), 56);
            var existingMonthStep = new MonthStep(testUser, now.getMonthValue(), now.getYear(), 56);

            stepRepository.save(existingStep);
            weekStepRepository.save(existingWeekStep);
            monthStepRepository.save(existingMonthStep);

            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));

            // Act
            stepService.addSingleStepForUser(testUser, testDto);

            // Expected Values
            var expected = 69;

            // Actual values
            var actualStepCount = stepRepository.getStepCountByUserIdAndDateRange(testUser, now.minusMinutes(5), now.plusMinutes(5));
            var actualWeekStepCount = weekStepRepository.getStepCountByUserIdYearAndWeek(testUser, now.getYear(), DateHelper.getWeek(now));
            var actualMonthStepCount = monthStepRepository.getStepCountByUserIdYearAndMonth(testUser, now.getYear(), now.getMonthValue());

            assertAll(
                    () -> assertTrue(actualStepCount.isPresent()),
                    () -> assertTrue(actualWeekStepCount.isPresent()),
                    () -> assertTrue(actualMonthStepCount.isPresent()),
                    () -> assertEquals(Optional.of(expected),actualStepCount),
                    () -> assertEquals(Optional.of(expected), actualWeekStepCount),
                    () -> assertEquals(Optional.of(expected), actualMonthStepCount)
            );
        }

        @Test
        @DisplayName("Should not create new step if step exists in database")
        public void testAddSingleStepForUser_DoesNotCreateNewStep() {
            // Arrange
            var existingStep = new Step(testUser, 13, now, now.plusMinutes(1), now.plusMinutes(2));
            stepRepository.save(existingStep);

            var newStepData = new StepDTO(56, now.plusMinutes(2), now.plusMinutes(3), now.plusMinutes(4));

            // Act
            stepService.addSingleStepForUser(testUser, newStepData);

            // Expected Values
            var expectedUserId = testUser;
            var expectedStepCount = 69;
            var expectedSteps = 1;

            // Actual values
            var stepsInDataBase = stepRepository.findAll();
            var actualUserId = stepsInDataBase.get(0).getUserId();
            var actualStepCount = stepsInDataBase.get(0).getStepCount();
            var actualSteps = stepsInDataBase.size();

            // Assert
            assertAll(
                    () -> assertEquals(expectedUserId, actualUserId),
                    () -> assertEquals(expectedStepCount, actualStepCount),
                    () -> assertEquals(expectedSteps, actualSteps)
            );
        }
    }

    @Nested
    @DisplayName("addMultipleStepsForUser method: ")
    public class AddMultipleStepsForUserTest {
        List<StepDTO> stepDtoList;

        @BeforeEach
        public void setUp() {
            stepDtoList = List.of(
                    new StepDTO(0, now, now.plusMinutes(1), now.plusMinutes(2)),
                    new StepDTO(2, now.plusMinutes(3), now.plusMinutes(4), now.plusMinutes(5)),
                    new StepDTO(3, now.plusMinutes(6), now.plusMinutes(7), now.plusMinutes(8))
            );
            stepDtoList.forEach(step -> step.setUserId(testUser));
        }

        @Test
        @DisplayName("Should return a list with an object of StepDTO class when no step exists in database for user")
        public void testAddMultipleStepsForUser_ReturnsListOfStepDTOObject() {
            // Act
            var result = stepService.addMultipleStepsForUser(testUser, stepDtoList).get(0);

            // Expected values
            Class<?> expectedClass = StepDTO.class;

            // Actual values
            Class<?> actualClass = result.getClass();

            // Assert
            assertNotNull(result, "Expected result not to be null but it was.");
            assertEquals(expectedClass, actualClass, "Expected class to be '" + expectedClass + "' but got " + actualClass);
        }

        @Test
        @DisplayName("Should return a list with size 1 when no step exists in database for user")
        public void testAddMultipleStepsForUser_ReturnsListOfSizeOne_WhenNoUserExistsInDatabase() {
            // Act
            var result = stepService.addMultipleStepsForUser(testUser, stepDtoList);

            // Expected values
            int expectedSize = 1;

            // Actual values
            int actualSize = result.size();

            // Assert
            assertNotNull(result, "Expected result not to be null but it was.");
            assertEquals(expectedSize, actualSize, "Expected list size to be '" + expectedSize + "' but got " + actualSize);
        }

        @Test
        @DisplayName("Returns 'Invalid Data' object when input userId is null")
        public void testAddMultipleStepsForUser_ReturnsCorrectObject_WhenUserIdInputIsNull() {
            // Act
            var result = stepService.addMultipleStepsForUser(null, stepDtoList).get(0);

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualUploadTime = result.getUploadTime();

            // Assert
            assertNotNull(result);
            assertEquals(expectedUserId, actualUserId, "Expected user id to be " + expectedUserId + " but was " + actualUserId);
            assertEquals(expectedUploadTime, actualUploadTime, "Expected upload time to be " + expectedUploadTime + " but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return correct object when stepDTO-input is null")
        public void testAddMultipleStepsForUser_ReturnsEmptyOptional_WhenTestDtoIsNull() {
            var now = LocalDateTime.now();

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, null).get(0);

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedUploadTime = now.getMinute();

            // Actual values
            var actualUserId = result.getUserId();
            var actualUploadTime = result.getUploadTime().getMinute();

            // Assert
            assertNotNull(result);
            assertEquals(expectedUserId, actualUserId, "Expected user id to be " + expectedUserId + " but was " + actualUserId);
            assertEquals(expectedUploadTime, actualUploadTime, "Expected upload time to be " + expectedUploadTime + " but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return step with correct field values if no Step is found in database")
        public void testAddMultipleStepsForUser_ReturnsObjectWithCorrectValues_IfNoStepIsFoundInDataBase() {
            // Arrange
            var testDto = new StepDTO(13, now, now.plusMinutes(1), now.plusMinutes(2));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            int expected = 13;

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expected, result.get().getStepCount(), "Expected step count to be '" + expected + "' but was " + result.get().getStepCount());
        }
    }
}