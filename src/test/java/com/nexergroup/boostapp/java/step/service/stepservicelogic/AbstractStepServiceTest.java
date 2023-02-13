package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.service.StepService;
import com.nexergroup.boostapp.java.step.testobjects.model.dto.stepdto.TestStepDtoBuilder;
import com.nexergroup.boostapp.java.step.testobjects.model.step.TestStepBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    String testUser = "testUser";
    Duration errorMargin = Duration.ofSeconds(1);

    TestStepDtoBuilder dtoBuilder = new TestStepDtoBuilder();
    TestStepBuilder stepBuilder = new TestStepBuilder();

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
            var testDto = dtoBuilder.createStepDTOWhereUserIdIsNull();

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
            assertTrue(Duration.between(expectedUploadTime, actualUploadTime).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " seconds of '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when stepDTO-input is null")
        public void testAddSingleStepForUser_ReturnsEmptyOptional_WhenTestDtoIsNull() {
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
            assertTrue(Duration.between(expectedUploadTime, result.getUploadedTime()).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return object of Step class when Step is used as input")
        public void testAddSingleStepForUser_ReturnsObjectOfStepClass() {
            // Arrange
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();

            // Act
            var actual = stepService.addSingleStepForUser(testUser, testDto).orElse(null);

            // Expected values
            var expectedClass = Step.class;

            // Assert
            assertNotNull(actual);
            assertEquals(expectedClass, actual.getClass(), () -> "Expected class to be '" + expectedClass + "' but got: " + actual.getClass());
        }

        @Test
        @DisplayName("Should create new Step if no Step is found in database")
        public void testAddSingleStepForUser_CreatesNewStep_IfNoStepIsFoundInDataBase() {
            // Arrange
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            int expectedStepCount = 10;

            // Assert
            assertTrue(result.isPresent(), "Expected a Step to be returned but it was empty");
            assertEquals(expectedStepCount, result.get().getStepCount(), "Expected step count to be '" + expectedStepCount + "' but was " + result.get().getStepCount());
        }

        @Test
        @DisplayName("Should update stepCount of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepCount_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(stepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = dtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedStepCount = 20 + 10;

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expectedStepCount, result.get().getStepCount(), "Expected step count to be '" + expectedStepCount + "' but was " + result.get().getStepCount());
        }

        @Test
        @DisplayName("Should update Step endTime of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepEndTime_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(stepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = dtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedEndTime = testDto.getEndTime();

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertTrue(Duration.between(expectedEndTime, result.get().getEndTime()).abs().compareTo(errorMargin) <= 0,
                    "Expected endTime to be within " + errorMargin + " second of '" + expectedEndTime + "' but was " + result.get().getEndTime());
        }

        @Test
        @DisplayName("Should update Step uploadTime of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepUploadTime_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(stepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = dtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedUploadTime = testDto.getUploadTime();

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertTrue(Duration.between(expectedUploadTime, result.get().getUploadedTime()).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.get().getUploadedTime());
        }

        @Test
        @DisplayName("Should return object with correct userID when creating new Step")
        public void testAddSingleStepForUser_ReturnsObjectWithCorrectValues_WhenCreatingNewStep() {
            // Arrange
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedUserId = testUser;

            // Assert
            assertTrue(result.isPresent(), "Expected a step to be returned but it was empty");
            assertEquals(expectedUserId, result.get().getUserId(), "Expected userId to be '" + expectedUserId + "'  but was " + result.get().getUserId());
        }

        @Test
        @DisplayName("Should return object with correct userId when updating current Step")
        public void testAddSingleStepForUser_ReturnsObjectWithUpdatedValues_WhenUpdatingStep() {
            // Arrange
            stepRepository.save(stepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = dtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedUserId = testUser;

            // Assert
            assertTrue(result.isPresent());
            assertEquals(expectedUserId, result.get().getUserId(), "Expected userId to be '" + expectedUserId + "' but was " + result.get().getUserId());
        }

        @Test
        @DisplayName("Should add Step stepCount to WeekStep-table")
        public void testAddSingleStepForUser_AddsStepCountToWeekStepTable() {
            // Arrange
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var actualStepCount = weekStepRepository.getStepCountByUserIdYearAndWeek(testUser, testDto.getEndTime().getYear(), DateHelper.getWeek(testDto.getEndTime()));

            // Expected values
            var expectedStepCount = 10;

            // Assert
            assertTrue(actualStepCount.isPresent(), "Expected step count to be returned but it was empty");
            assertEquals(Optional.of(expectedStepCount), actualStepCount, "Expected step count to be '" + expectedStepCount + "'  but got " + actualStepCount.get());
        }

        @Test
        @DisplayName("Should store all WeekStep-fields in database correctly")
        public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToWeekStepTable() {
            // Arrange
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, testDto.getEndTime().getYear(), DateHelper.getWeek(testDto.getEndTime()));

            // Expected values
            var expectedUserId = testUser;
            var expectedYear = testDto.getYear();
            var expectedWeek = DateHelper.getWeek(testDto.getEndTime());
            var expectedStepCount = testDto.getStepCount();

            // Actual values
            var actualUserId = result.get().getUserId();
            var actualYear = result.get().getYear();
            var actualWeek = result.get().getWeek();
            var actualStepCount = result.get().getStepCount();

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
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = monthStepRepository.findByUserIdAndYearAndMonth(testUser, testDto.getEndTime().getYear(), testDto.getEndTime().getMonthValue());

            // Expected values
            var expectedStepCount = 10;

            // Assert
            assertTrue(result.isPresent(), "Expected step count to be returned but it was empty");
            assertEquals(expectedStepCount, result.get().getStepCount(), "Expected step count to be '" + expectedStepCount + "' but got " + result.get());
        }

        @Test
        @DisplayName("Should store all MonthStep-fields in database correctly")
        public void testAddSingleStepForUser_AddsAllFieldsCorrectlyToMonthStepTable() {
            // Arrange
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = monthStepRepository.findByUserIdAndYearAndMonth(testUser, testDto.getEndTime().getYear(), testDto.getEndTime().getMonthValue());

            // Expected values
            var expectedUserId = testUser;
            var expectedYear = testDto.getYear();
            var expectedMonth = testDto.getEndTime().getMonthValue();
            var expectedStepCount = testDto.getStepCount();

            // Actual values
            var actualUserId = result.get().getUserId();
            var actualYear = result.get().getYear();
            var actualMonth = result.get().getMonth();
            var actualStepCount = result.get().getStepCount();

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
        @DisplayName("Should return Step with correctly updated values after first step is added")
        public void testAddSingleStepForUser_UpdatesStepCorrectly() {
            // Arrange
            var now = LocalDateTime.now();
            var testDto = dtoBuilder.createStepDTOOfFirstMinuteOfYear();
            var additionalTestDto =dtoBuilder.createStepDTOOfSecondMinuteOfYear();

            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var savedStep = stepService.addSingleStepForUser(testUser, additionalTestDto);

            // Expected values
            var expectedUserId = testUser;
            var expectedStartTime = testDto.getStartTime();
            var expectedEndTime = additionalTestDto.getEndTime();
            var expectedUploadTime = additionalTestDto.getUploadTime();

            // Actual values
            var actualUserId = savedStep.get().getUserId();
            var actualStartTime = savedStep.get().getStartTime();
            var actualEndTime = savedStep.get().getEndTime();
            var actualUploadTime = savedStep.get().getUploadedTime();

            // Assert
            assertAll(
                    () -> assertTrue(savedStep.isPresent(), "Expected a step to be returned but it was empty"),
                    () -> assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but was " + actualUserId),
                    () -> assertTrue(Duration.between(expectedStartTime, actualStartTime).abs().compareTo(errorMargin) <= 0,
                            "Expected startTime to be within 1 second of '" + expectedStartTime + "' but was " + actualStartTime),
                    () -> assertTrue(Duration.between(expectedEndTime, actualEndTime).abs().compareTo(errorMargin) <= 0,
                            "Expected endTime to be within 1 second of '" + expectedEndTime + "' but was " + actualEndTime),
                    () -> assertTrue(Duration.between(expectedUploadTime, actualUploadTime).abs().compareTo(errorMargin) <= 0,
                            "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + actualUploadTime)
            );
        }

        @Test
        @DisplayName("Returns 'Invalid Data' object when DTO with invalid time fields is used as input")
        public void testAddSingleStepForUser_ReturnsOptionalEmpty_WhenTimeValueIsIncorrect() {
            // Arrange
            var badTestDto = dtoBuilder.createStepDTOWhereTimeFieldsAreIncompatible();

            // Act
            var result = stepService.addSingleStepForUser(testUser, badTestDto).get();

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
            assertTrue(Duration.between(expectedUploadTime, actualUploadTime).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should add stepCount to all tables when no step exists for user in database")
        public void testAddingSingleStepForUser_AddsStepCountToAllTables() {
            // Arrange
            var stepDTO = dtoBuilder.createStepDTOOfFirstMinuteOfYear();

            // Act
            stepService.addSingleStepForUser(testUser, stepDTO);

            // Expected Values
            var expectedStepCount = 10;

            // Actual values
            var actualSingleStepCount = stepRepository.getStepCountByUserIdAndDateRange(testUser, stepDTO.getStartTime().minusMinutes(1), stepDTO.getUploadTime().plusMinutes(1));
            var actualWeekStepCount = weekStepRepository.getStepCountByUserIdYearAndWeek(testUser, stepDTO.getEndTime().getYear(), DateHelper.getWeek(stepDTO.getEndTime()));
            var actualMonthStepCount = monthStepRepository.getStepCountByUserIdYearAndMonth(testUser, stepDTO.getEndTime().getYear(), stepDTO.getEndTime().getMonthValue());

            // Assert
            assertAll(
                    () -> assertTrue(actualSingleStepCount.isPresent()),
                    () -> assertTrue(actualWeekStepCount.isPresent()),
                    () -> assertTrue(actualMonthStepCount.isPresent()),
                    () -> assertEquals(Optional.of(expectedStepCount), actualSingleStepCount, "Expected Step stepCount to be '" + expectedStepCount + "' but was " + actualSingleStepCount),
                    () -> assertEquals(Optional.of(expectedStepCount), actualWeekStepCount, "Expected WeekStep stepCount to be '" + expectedStepCount + "' but was " + actualWeekStepCount),
                    () -> assertEquals(Optional.of(expectedStepCount), actualMonthStepCount, "Expected MonthStep stepCount to be '" + expectedStepCount + "' but was " + actualMonthStepCount)
            );
        }

        @Test
        @DisplayName("Should update stepCount of all tables when step exists for user in database")
        public void testAddingSingleStepForUser_UpdatesStepCountOfAllTables() {
            // Arrange
            var existingStep = stepBuilder.createStepOfFirstMinuteOfYear();
            var existingWeekStep = new WeekStep(testUser, DateHelper.getWeek(existingStep.getEndTime()), existingStep.getEndTime().getYear(), 10);
            var existingMonthStep = new MonthStep(testUser, existingStep.getEndTime().getMonthValue(), existingStep.getEndTime().getYear(), 10);

            stepRepository.save(existingStep);
            weekStepRepository.save(existingWeekStep);
            monthStepRepository.save(existingMonthStep);

            var testDto = dtoBuilder.createStepDTOOfSecondMinuteOfYear();
            // Act
            stepService.addSingleStepForUser(testUser, testDto);

            // Expected Values
            var expectedStepCount = 30;

            // Actual values
            var actualStepStepCount = stepRepository.getStepCountByUserIdAndDateRange(testUser, testDto.getEndTime().minusMinutes(1), testDto.getEndTime().plusMinutes(1));
            var actualWeekStepCount = weekStepRepository.getStepCountByUserIdYearAndWeek(testUser, testDto.getEndTime().getYear(), DateHelper.getWeek(testDto.getEndTime()));
            var actualMonthStepCount = monthStepRepository.getStepCountByUserIdYearAndMonth(testUser, testDto.getEndTime().getYear(), testDto.getEndTime().getMonthValue());

            assertAll(
                    () -> assertTrue(actualStepStepCount.isPresent(), "Expected Step stepCount to be returned but was empty"),
                    () -> assertTrue(actualWeekStepCount.isPresent(), "Expected WeekStep stepCount to be returned but was empty"),
                    () -> assertTrue(actualMonthStepCount.isPresent(), "Expected MonthSped stepCount to be returned but was empty"),
                    () -> assertEquals(Optional.of(expectedStepCount), actualStepStepCount, "Expected Step stepCount to be '" + expectedStepCount + "' but was " + actualStepStepCount),
                    () -> assertEquals(Optional.of(expectedStepCount), actualWeekStepCount, "Expected WeekStep stepCount to be '" + expectedStepCount + "' but was " + actualWeekStepCount),
                    () -> assertEquals(Optional.of(expectedStepCount), actualMonthStepCount, "Expected MonthStep stepCount to be '" + expectedStepCount + "' but was " + actualMonthStepCount)
            );
        }

        @Test
        @DisplayName("Should update Step object in database")
        public void shouldReturnUpdatedStepCount() {

            // Arrange

            var testStep = stepBuilder.createStepOfFirstMinuteOfYear();

            var testDTO = dtoBuilder.createStepDTOOfSecondMinuteOfYear();

            stepRepository.save(testStep);

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDTO);

            // Expected Values
            Integer expectedStepCount = 30;

            // Actual values
            var actualStepCount = result.map(Step::getStepCount)
                    .orElse(0);

            // Assert
            assertEquals(expectedStepCount, actualStepCount, "userId  " + result.get().getUserId());
        }

        @Test
        @DisplayName("Should not create new step if step exists in database")
        public void testAddSingleStepForUser_DoesNotCreateNewStep() {
            // Arrange
            var now = LocalDateTime.now();
            var existingStep = stepBuilder.createStepOfFirstMinuteOfYear();
            stepRepository.save(existingStep);

            var testDto = dtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            stepService.addSingleStepForUser(testUser, testDto);

            // Expected Values
            var expectedUserId = testUser;
            var expectedStepCount = 30;
            var expectedNumberOfSteps = 1;

            // Actual values
            var stepsInDataBase = stepRepository.findAll();
            var actualUserId = stepsInDataBase.get(0).getUserId();
            var actualStepCount = stepsInDataBase.get(0).getStepCount();
            var actualNumberOfSteps = stepsInDataBase.size();

            // Assert
            assertAll(
                    () -> assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but was " + actualUserId),
                    () -> assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount),
                    () -> assertEquals(expectedNumberOfSteps, actualNumberOfSteps, "Expected numberOfSteps to be '" + expectedUserId + "' but was " + actualUserId)
            );
        }
    }

    @Nested
    @DisplayName("addMultipleStepsForUser method: ")
    public class AddMultipleStepsForUserTest {
        List<StepDTO> stepDtoList = new ArrayList<>();

        @BeforeEach
        public void setUp() {
            var now = LocalDateTime.now();
            stepDtoList.add(dtoBuilder.createStepDTOOfFirstMinuteOfYear());
            stepDtoList.add(dtoBuilder.createStepDTOOfSecondMinuteOfYear());
            stepDtoList.add(dtoBuilder.createStepDTOOfThirdMinuteOfYear());
        }

        @Test
        @DisplayName("Should return a list with an object of StepDTO class when no step exists in database for user")
        public void testAddMultipleStepsForUser_ReturnsListOfStepDTOObject() {
            // Act
            var result = stepService.addMultipleStepsForUser(testUser, stepDtoList);

            // Expected values
            Class<?> expectedClass = StepDTO.class;

            // Actual values
            Class<?> actualClass = result.getClass();

            // Assert
            assertNotNull(result, "Expected result not to be null but it was.");
            assertEquals(expectedClass, actualClass, "Expected class to be '" + expectedClass + "' but got " + actualClass);
        }

        @Test
        @DisplayName("Should return a DTO object with the total stepCount of the StepDTO:s in the list passed as input")
        public void testAddMultipleStepsForUser_ReturnsListOfSizeOne_WhenNoUserExistsInDatabase() {
            // Act
            var result = stepService.addMultipleStepsForUser(testUser, stepDtoList);

            // Expected values
            int expectedStepCount = 10 + 20 + 30;

            // Actual values
            int actualStepCount = result.getStepCount();

            // Assert
            assertNotNull(result, "Expected result not to be null but it was.");
            System.out.println(stepDtoList);
            assertEquals(expectedStepCount, actualStepCount,"Expected stepCount to be '" + expectedStepCount + "' but got " + actualStepCount);
        }

        @Test
        @DisplayName("Returns 'Invalid Data' object when input userId is null")
        public void testAddMultipleStepsForUser_ReturnsCorrectObject_WhenUserIdInputIsNull() {
            // Act
            var result = stepService.addMultipleStepsForUser(null, stepDtoList);

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualUploadTime = result.getUploadTime();

            // Assert
            assertNotNull(result);
            assertEquals(expectedUserId, actualUserId, "Expected user id to be " + expectedUserId + " but was " + actualUserId);
            assertTrue(Duration.between(expectedUploadTime, actualUploadTime).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return correct object when stepDTO-input is null")
        public void testAddMultipleStepsForUser_ReturnsEmptyOptional_WhenTestDtoIsNull() {
            // Act
            var result = stepService.addMultipleStepsForUser(testUser, null);

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualUploadTime = result.getUploadTime();

            // Assert
            assertNotNull(result);
            assertEquals(expectedUserId, actualUserId, "Expected user id to be " + expectedUserId + " but was " + actualUserId);
            assertTrue(Duration.between(expectedUploadTime, actualUploadTime).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + actualUploadTime);
        }
    }
}
