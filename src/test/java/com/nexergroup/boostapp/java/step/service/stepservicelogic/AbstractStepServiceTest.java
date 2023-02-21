package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.service.StepService;
import com.nexergroup.boostapp.java.step.testobjects.dto.stepdto.TestStepDtoBuilder;
import com.nexergroup.boostapp.java.step.testobjects.model.TestStepBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    TestStepDtoBuilder testDtoBuilder = new TestStepDtoBuilder();
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


        @Test
        @DisplayName("Should return 'Invalid Data' object when userId-input is null")
        public void testAddSingleStepForUser_ReturnsCorrectObject_WhenUserIdIsNull() {
            // Arrange
            var testDto = testDtoBuilder.createStepDTOWhereUserIdIsNull();

            // Act
            var result = stepService.addSingleStepForUser(null, testDto);

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
        public void testAddSingleStepForUser_ReturnsCorrectObject_WhenTestDtoIsNull() {
            // Act
            var result = stepService.addSingleStepForUser(testUser, null);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when stepDTO startTime-field is null")
        public void testAddSingleStepForUser_ReturnsCorrectObject_WhenDtoStartTimeIsNull() {
            // Arrange
            var testDTO = testDtoBuilder.createStepDTOWhereStartTimeIsNull();
            // Act
            var result = stepService.addSingleStepForUser(testUser, testDTO);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when stepDTO endTime-field is null")
        public void testAddSingleStepForUser_ReturnsCorrectObject_WhenDtoEndTimeIsNull() {
            // Arrange
            var testDTO = testDtoBuilder.createStepDTOWhereEndTimeIsNull();
            // Act
            var result = stepService.addSingleStepForUser(testUser, testDTO);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when stepDTO uploadTime-field is null")
        public void testAddSingleStepForUser_ReturnsCorrectObject_WhenDtoUploadTimeIsNull() {
            // Arrange
            var testDTO = testDtoBuilder.createStepDTOWhereUploadTimeIsNull();
            // Act
            var result = stepService.addSingleStepForUser(testUser, testDTO);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return object of Step class when Step is used as input")
        public void testAddSingleStepForUser_ReturnsObjectOfStepClass() {
            // Arrange
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();

            // Act
            var actual = stepService.addSingleStepForUser(testUser, testDto);

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
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            int expectedStepCount = 10;

            // Assert
            assertEquals(expectedStepCount, result.getStepCount(), "Expected step count to be '" + expectedStepCount + "' but was " + result.getStepCount());
        }

        @Test
        @DisplayName("Should update stepCount of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepCount_IfStepIsFoundInDataBase() {
            // Arrange
             var existingStep = stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedStepCount = 20 + 10;

            // Assert
            assertEquals(expectedStepCount, result.getStepCount(), "Expected step count to be '" + expectedStepCount + "' but was " + result.getStepCount());
        }

        @Test
        @DisplayName("Should update Step endTime of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepEndTime_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedEndTime = testDto.getEndTime();

            // Assert
            assertTrue(Duration.between(expectedEndTime, result.getEndTime()).abs().compareTo(errorMargin) <= 0,
                    "Expected endTime to be within " + errorMargin + " second of '" + expectedEndTime + "' but was " + result.getEndTime());
        }

        @Test
        @DisplayName("Should update Step uploadTime of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepUploadTime_IfStepIsFoundInDataBase() {
            // Arrange
            stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedUploadTime = testDto.getUploadTime();

            // Assert
            assertTrue(Duration.between(expectedUploadTime, result.getUploadedTime()).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return object with correct userID when creating new Step")
        public void testAddSingleStepForUser_ReturnsObjectWithCorrectValues_WhenCreatingNewStep() {
            // Arrange
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedUserId = testUser;

            // Assert
            assertEquals(expectedUserId, result.getUserId(), "Expected userId to be '" + expectedUserId + "'  but was " + result.getUserId());
        }

        @Test
        @DisplayName("Should return object with correct userId when updating current Step")
        public void testAddSingleStepForUser_ReturnsObjectWithUpdatedValues_WhenUpdatingStep() {
            // Arrange
            stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values
            var expectedUserId = testUser;

            // Assert
            assertEquals(expectedUserId, result.getUserId(), "Expected userId to be '" + expectedUserId + "' but was " + result.getUserId());
        }

        @Test
        @DisplayName("Should add Step stepCount to WeekStep-table")
        public void testAddSingleStepForUser_AddsStepCountToWeekStepTable() {
            // Arrange
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();
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
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = weekStepRepository.findByUserIdAndYearAndWeek(testUser, testDto.getEndTime().getYear(), DateHelper.getWeek(testDto.getEndTime()));

            // Expected values
            var expectedUserId = testUser;
            var expectedYear = testDto.getYear();
            var expectedWeek = DateHelper.getWeek(testDto.getEndTime());
            var expectedStepCount = testDto.getStepCount();

            // Actual values
            var actualUserId = result.orElseThrow().getUserId();
            var actualYear = result.orElseThrow().getYear();
            var actualWeek = result.orElseThrow().getWeek();
            var actualStepCount = result.orElseThrow().getStepCount();

            // Assert
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
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();
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
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();
            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var result = monthStepRepository.findByUserIdAndYearAndMonth(testUser, testDto.getEndTime().getYear(), testDto.getEndTime().getMonthValue());

            // Expected values
            var expectedUserId = testUser;
            var expectedYear = testDto.getYear();
            var expectedMonth = testDto.getEndTime().getMonthValue();
            var expectedStepCount = testDto.getStepCount();

            // Actual values
            var actualUserId = result.orElseThrow().getUserId();
            var actualYear = result.orElseThrow().getYear();
            var actualMonth = result.orElseThrow().getMonth();
            var actualStepCount = result.orElseThrow().getStepCount();

            // Assert
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
            var testDto = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();
            var additionalTestDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            additionalTestDto.setStartTime(testDto.getEndTime().minusSeconds(10));

            stepService.addSingleStepForUser(testUser, testDto);

            // Act
            var savedStep = stepService.addSingleStepForUser(testUser, additionalTestDto);

            // Expected values
            var expectedUserId = testUser;
            var expectedStartTime = testDto.getStartTime();
            var expectedEndTime = additionalTestDto.getEndTime();
            var expectedUploadTime = additionalTestDto.getUploadTime();

            // Actual values
            var actualUserId = savedStep.getUserId();
            var actualStartTime = savedStep.getStartTime();
            var actualEndTime = savedStep.getEndTime();
            var actualUploadTime = savedStep.getUploadedTime();

            // Assert
            assertAll(
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
        public void testAddSingleStepForUser_ReturnsCorrectObject_WhenTimeValueIsIncorrect() {
            // Arrange
            var badTestDto = testDtoBuilder.createStepDTOWhereTimeFieldsAreIncompatible();

            // Act
            var result = stepService.addSingleStepForUser(testUser, badTestDto);

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
            var stepDTO = testDtoBuilder.createStepDTOOfFirstMinuteOfYear();

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
            var existingStep = testStepBuilder.createStepOfFirstMinuteOfYear();
            var existingWeekStep = testStepBuilder.createWeekStepOfStep(existingStep);
            var existingMonthStep = testStepBuilder.createMonthStepOfStep(existingStep);

            stepRepository.save(existingStep);
            weekStepRepository.save(existingWeekStep);
            monthStepRepository.save(existingMonthStep);

            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));

            // Act
            stepService.addSingleStepForUser(testUser, testDto);

            // Expected Values
            var expectedStepCount = 30;

            // Actual values
            var actualStepStepCount = stepRepository.findById(existingStep.getId()).orElseThrow().getStepCount();
            var actualWeekStepCount = weekStepRepository.findById(existingWeekStep.getId()).orElseThrow().getStepCount();
            var actualMonthStepCount = monthStepRepository.findById(existingMonthStep.getId()).orElseThrow().getStepCount();

            assertEquals(expectedStepCount, actualStepStepCount, "Expected Step stepCount to be '" + expectedStepCount + "' but was " + actualStepStepCount);
            assertEquals(expectedStepCount, actualWeekStepCount, "Expected WeekStep stepCount to be '" + expectedStepCount + "' but was " + actualWeekStepCount);
            assertEquals(expectedStepCount, actualMonthStepCount, "Expected MonthStep stepCount to be '" + expectedStepCount + "' but was " + actualMonthStepCount);

        }

        @Test
        @DisplayName("Should update stepCount of Step in database when adding new step data")
        public void testAddSingleStepForUser_UpdatesStepInDateBase() {
            // Arrange
            var testStep = testStepBuilder.createStepOfFirstMinuteOfYear();
            var testDTO = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDTO.setStartTime(testStep.getEndTime().minusSeconds(10));

            stepRepository.save(testStep);

            // Act
            var result = stepService.addSingleStepForUser(testUser, testDTO);

            // Expected Values
            int expectedStepCount = 30;

            // Actual values
            var actualStepCount = result.getStepCount();

            // Assert
            assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be " + expectedStepCount + " but was " + actualStepCount);
        }

        @Test
        @DisplayName("Should not create new step if step exists in database")
        public void testAddSingleStepForUser_DoesNotCreateNewStep() {
            // Arrange
            var existingStep = testStepBuilder.createStepOfFirstMinuteOfYear();
            stepRepository.save(existingStep);

            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));

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

        @Test
        @DisplayName("Returns 'Invalid Data' object when input userId is null")
        public void testAddMultipleStepsForUser_ReturnsCorrectObject_WhenUserIdInputIsNull() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(null, testList);

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualUploadTime = result.getUploadedTime();

            // Assert
            assertNotNull(result);
            assertEquals(expectedUserId, actualUserId, "Expected user id to be " + expectedUserId + " but was " + actualUserId);
            assertTrue(Duration.between(expectedUploadTime, actualUploadTime).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when stepDTO-list used as input is null")
        public void testAddMultipleStepsForUser_ReturnsCorrectObject_WhenTestDtoListIsNull() {
            // Act
            var result = stepService.addMultipleStepsForUser(testUser, null);

            // Expected values
            var expectedUserId = "Invalid Data";
            var expectedUploadTime = LocalDateTime.now();

            // Actual values
            var actualUserId = result.getUserId();
            var actualUploadTime = result.getUploadedTime();

            // Assert
            assertNotNull(result);
            assertEquals(expectedUserId, actualUserId, "Expected user id to be " + expectedUserId + " but was " + actualUserId);
            assertTrue(Duration.between(expectedUploadTime, actualUploadTime).abs().compareTo(errorMargin) <= 0,
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + actualUploadTime);
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when list used as input contains a single stepDTO where startTime-field is null")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenDtoInListHasNullStartTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOWhereStartTimeIsNull()));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when list used as input contains a single stepDTO where endTime-field is null")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenDtoInListHasNullEndTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOWhereEndTimeIsNull()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when list used as input contains a single stepDTO where uploadTime-field is null")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenDtoInListHasNullUploadTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOWhereUploadTimeIsNull()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when first of several objects in input list has a null startTime-field")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenFirstOfSeveralDtoHasNullStartTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOWhereStartTimeIsNull(),
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear())
            );

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when last of several objects in input list has a null startTime-field")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenLastOfSeveralDtoHasNullStartTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear(),
                    testDtoBuilder.createStepDTOWhereEndTimeIsNull()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when first of several objects in input list has a null endTime-field")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenFirstOfSeveralDtoHasNullEndTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOWhereEndTimeIsNull(),
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when last of several objects in input list has a null endTime-field")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenLastOfSeveralDtoHasNullEndTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear(),
                    testDtoBuilder.createStepDTOWhereEndTimeIsNull()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when first of several objects in input list has a null uploadTime-field")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenFirstOfSeveralDtoHasNullUploadTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOWhereUploadTimeIsNull(),
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return 'Invalid Data' object when last of several objects in input list has a null uploadTime-field")
        public void addMultipleStepsForUser_ReturnsCorrectObject_WhenLastOfSeveralDtoHasNullUploadTime() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear(),
                    testDtoBuilder.createStepDTOWhereUploadTimeIsNull()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

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
                    "Expected uploadTime to be within " + errorMargin + " second of '" + expectedUploadTime + "' but was " + result.getUploadedTime());
        }

        @Test
        @DisplayName("Should return a list with an object of Step  class when no step exists in database for user")
        public void testAddMultipleStepsForUser_ReturnsListOfStepObject() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear()));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

            // Expected values
            Class<?> expectedClass = Step.class;

            // Actual values
            Class<?> actualClass = result.getClass();

            // Assert
            assertNotNull(result, "Expected result not to be null but it was.");
            assertEquals(expectedClass, actualClass, "Expected class to be '" + expectedClass + "' but got " + actualClass);
        }

        @Test
        @DisplayName("Should return a StepDTO object with the total stepCount of the StepDTO:s in the list passed as input")
        public void testAddMultipleStepsForUser_ReturnsObjectWithCorrectStepCount_WhenNoUserExistsInDatabase() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear()
            ));

            // Act
            var result = stepService.addMultipleStepsForUser(testUser, testList);

            // Expected values
            int expectedStepCount = 10 + 20 + 30;

            // Actual values
            int actualStepCount = result.getStepCount();

            // Assert
            assertNotNull(result, "Expected result not to be null but it was.");
            assertEquals(expectedStepCount, actualStepCount,"Expected stepCount to be '" + expectedStepCount + "' but got " + actualStepCount);
        }
    }

    @Nested
    @DisplayName("getStepCountForUserYearAndMonth method:")
    public class GetStepCountForUserYearAndMonthTest {

        @Test
        @DisplayName("Test one")
        public void testGetStepCountForUserYearAndMonthTest_ReturnsCorrectStepCount() {
            // Arrange
            monthStepRepository.save(testStepBuilder.createMonthStepOfFirstMonthOfYear());

            // Act
            var result = stepService.getStepCountForUserYearAndMonth(testUser, 2023, 1);

            // Expected values
            var expectedStepCount = 10;

            // Actual values
            var actualStepCount = (int)result;

            // Assert
            assertEquals(expectedStepCount, actualStepCount);
        }
    }
}
