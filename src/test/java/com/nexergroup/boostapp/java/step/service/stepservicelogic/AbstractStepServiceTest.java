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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
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
        @DisplayName("Should return Exception when userId-input is null")
        public void testAddSingleStepForUser_ReturnsEmptyOptional_WhenUserIdIsNull() {
            // Create a StepDTO where userId is null
            var testDto = testDtoBuilder.createStepDTOWhereUserIdIsNull();

            // Act
            Exception exception = assertThrows(ValidationFailedException.class, () -> {
                stepService.addSingleStepForUser(null, testDto);
            });

            // Expected values
            String expectedMessage = "userId null. Validation for step failed";

            // Actual values
            String actualMessage = exception.getMessage();

            // Assert
            assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        @DisplayName("Should return Exception when stepDTO-input is null")
        public void testAddSingleStepForUser_ReturnsEmptyOptional_WhenTestDtoIsNull() {
            // Act
            Exception exception = assertThrows(ValidationFailedException.class, () -> {
                stepService.addSingleStepForUser(testUser, null);
            });

            // Expected values
            String expectedMessage = "Step object is empty";

            // Actual values
            String actualMessage = exception.getMessage();

            // Assert
            assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        @DisplayName("Should return object of StepDTO class when Step is used as input")
        public void testAddSingleStepForUser_ReturnsObjectOfStepDtoClass() {
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
        @DisplayName("Should return Step with updated stepCount if Step object in database is updated")
        public void testAddSingleStepForUser_UpdatesStepCount_IfStepIsFoundInDataBase() {
            // Create test Step objects and save them to all tables of database. Step creation is dependent on data existing in all tables or no tables at all
            var existingStep = stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            weekStepRepository.save(testStepBuilder.createWeekStepOfStep(existingStep));
            monthStepRepository.save(testStepBuilder.createMonthStepOfStep(existingStep));

            // Create the new data to be added to the database and set the startTime to before the endTime of the stored Step object
            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));

            // Use the test data on the method to be tested
            var result = stepService.addSingleStepForUser(testUser, testDto);

            // Expected values: stepCount of testDTO + stepCount of existing Step object
            var expectedStepCount = 20 + 10;

            // Assert
            assertEquals(expectedStepCount, result.getStepCount(), "Expected stepCount to be '" + expectedStepCount + "' but was " + result.getStepCount());
        }

        @Test
        @DisplayName("Should update endTime of Step passed as input if active Step is found in database")
        public void testAddSingleStepForUser_UpdatesStepEndTime_IfStepIsFoundInDataBase() {
            // Create test Step objects and save them to all tables of database. Step creation is dependent on data existing in all tables or no tables at all
            var existingStep = stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            weekStepRepository.save(testStepBuilder.createWeekStepOfStep(existingStep));
            monthStepRepository.save(testStepBuilder.createMonthStepOfStep(existingStep));

            // Create the new data to be added to the database and set the startTime to before the endTime of the stored Step object
            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));

            // Use the test data on the method to be tested
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
            // Create test Step objects and save them to all tables of database. Step creation is dependent on data existing in all tables or no tables at all
            var existingStep = stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            weekStepRepository.save(testStepBuilder.createWeekStepOfStep(existingStep));
            monthStepRepository.save(testStepBuilder.createMonthStepOfStep(existingStep));

            // Create the new data to be added to the database and set the startTime to before the endTime of the stored Step object
            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));
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
// Create test Step objects and save them to all tables of database. Step creation is dependent on data existing in all tables or no tables at all
            var existingStep = stepRepository.save(testStepBuilder.createStepOfFirstMinuteOfYear());
            weekStepRepository.save(testStepBuilder.createWeekStepOfStep(existingStep));
            monthStepRepository.save(testStepBuilder.createMonthStepOfStep(existingStep));

            var testDto = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));

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
        @DisplayName("Throws exception when DTO with invalid time fields is used as input")
        public void testAddSingleStepForUser_ReturnsOptionalEmpty_WhenTimeValueIsIncorrect() {
            // Arrange
            var badTestDto = testDtoBuilder.createStepDTOWhereTimeFieldsAreIncompatible();

            // Act
            Exception exception = assertThrows(DateTimeValueException.class, () -> {
                stepService.addSingleStepForUser(testUser, badTestDto);
            });

            // Expected values
            String expectedMessage = "Start time must be before end time";

            // Actual values
            String actualMessage = exception.getMessage();

            // Assert
            assertTrue(actualMessage.contains(expectedMessage));
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
        @DisplayName("Should update stepCount of Step object in database when adding new step data if timeFields overlap")
        public void testAddSingleStepForUser_UpdatesStepInDateBase() {
            // Create test Step objects and save them to all tables of database. Step creation is dependent on data existing in all tables or no tables at all
            var existingStep = testStepBuilder.createStepOfFirstMinuteOfYear();
            weekStepRepository.save(testStepBuilder.createWeekStepOfStep(existingStep));
            monthStepRepository.save(testStepBuilder.createMonthStepOfStep(existingStep));

            // Create the new data to be added to the database and set the startTime to before the endTime of the stored Step object
            var testDTO = testDtoBuilder.createStepDTOOfSecondMinuteOfYear();
            testDTO.setStartTime(existingStep.getEndTime().minusSeconds(10));

            // Use the test data on the method to be tested
            stepService.addSingleStepForUser(testUser, testDTO);

            // Expected Values
            var result = stepRepository.findFirstByUserIdOrderByEndTimeDesc(testUser).orElseThrow();
            int expectedStepCount = result.getStepCount();

            // Actual values
            var actualStepCount = result.getStepCount();

            // Assert
            assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be " + expectedStepCount + " but was " + actualStepCount);
        }

        @Test
        @DisplayName("Should not create new Step object if startTime of StepDTO is before endTime of Step")
        public void testAddSingleStepForUser_DoesNotCreateNewStep_WhenStartTimeIsBeforeEndTime() {
            // Create test Step objects and save them to all tables of database. Step creation is dependent on data existing in all tables or no tables at all
            var existingStep = testStepBuilder.createStepOfFirstMinuteOfYear();
            stepRepository.save(existingStep);
            weekStepRepository.save(testStepBuilder.createWeekStepOfStep(existingStep));
            monthStepRepository.save(testStepBuilder.createMonthStepOfStep(existingStep));

            // Create the new data to be added to the database and set the startTime to before the endTime of the stored Step object
            var testDto = testDtoBuilder.createStepDTOOfThirdMinuteOfYear();
            testDto.setStartTime(existingStep.getEndTime().minusSeconds(10));

            // Use the test data on the method to be tested
            stepService.addSingleStepForUser(testUser, testDto);

            // Expected Values: The correct userId,  total stepCount and the number of objects found in database
            var expectedUserId = testUser;
            var expectedStepCount = 40;
            var expectedNumberOfStepObjects = 1;

            // Actual values
            var objectsInDataBase = stepRepository.findAll();

            var actualUserId = objectsInDataBase.get(0).getUserId();
            var actualStepCount = objectsInDataBase.get(0).getStepCount();
            var actualNumberOfStepObjects = objectsInDataBase.size();

            // Assert
            assertEquals(expectedUserId, actualUserId, "Expected userId to be '" + expectedUserId + "' but was " + actualUserId);
            assertEquals(expectedStepCount, actualStepCount, "Expected stepCount to be '" + expectedStepCount + "' but was " + actualStepCount);
            assertEquals(expectedNumberOfStepObjects, actualNumberOfStepObjects, "Expected numberOfSteps to be '" + expectedUserId + "' but was " + actualUserId);
        }
    }

    @Nested
    @DisplayName("addMultipleStepsForUser method: ")
    public class AddMultipleStepsForUserTest {


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

        @Test
        @DisplayName("Throws exception when input userId is null")
        public void testAddMultipleStepsForUser_ThrowsException_WhenUserIdInputIsNull() {
            // Arrange
            List<StepDTO> testList = new ArrayList<>(List.of(
                    testDtoBuilder.createStepDTOOfFirstMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfSecondMinuteOfYear(),
                    testDtoBuilder.createStepDTOOfThirdMinuteOfYear()
            ));

            // Act
            Exception exception = assertThrows(ValidationFailedException.class, () -> {
                stepService.addMultipleStepsForUser(null, testList);
            });

            // Expected values
            String expectedMessage = "UserId is null. Validation for step failed";

            // Actual values
            String actualMessage = exception.getMessage();

            // Assert
            assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        @DisplayName("Should throw exception when stepDTO-input is null")
        public void testAddMultipleStepsForUser_ReturnsEmptyOptional_WhenTestDtoIsNull() {
            // Act
            Exception exception = assertThrows(ValidationFailedException.class, () -> {
                stepService.addMultipleStepsForUser(testUser, null);
            });

            // Expected values
            String expectedMessage = "UserId is null. Validation for step failed";

            // Actual values
            String actualMessage = exception.getMessage();

            // Assert
            assertTrue(actualMessage.contains(expectedMessage));
        }
    }
}
