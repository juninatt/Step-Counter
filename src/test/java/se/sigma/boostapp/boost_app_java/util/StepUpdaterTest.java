package se.sigma.boostapp.boost_app_java.util;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.BoostAppStep;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName(" <== ObjectUpdater ==>")
public class StepUpdaterTest {

    private final LocalDateTime START_TIME = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
    private final LocalDateTime END_TIME = LocalDateTime.of(2023, 1, 1, 2, 1, 1);
    private final LocalDateTime UPLOADED_TIME = LocalDateTime.of(2023, 1, 1, 3, 1, 1);

    private StepDTO testDto;
    private Step testStep;
    private MonthStep testMonthStep;
    private WeekStep testWeekStep;
    private StepUpdater stepUpdater;

    @BeforeEach
    public void setUp() {
        stepUpdater = StepUpdater.getInstance();

        testDto = new StepDTO(69, START_TIME, END_TIME, UPLOADED_TIME);
        testStep = new Step("testStep", 100, END_TIME);
        int YEAR = 2023;
        int MONTH = 1;
        testMonthStep = new MonthStep("testStep", MONTH, YEAR, 100);
        int WEEK = 1;
        testWeekStep = new WeekStep("testStep", WEEK, YEAR, 100);
    }
    @Nested
    @DisplayName("updateCurrentStep ")
    class UpdateCurrentStepTest {

        @Test
        @DisplayName("should return object of Step class")
        public void testUpdateCurrentStep_ReturnsObjectOfStepClass() {
            var updatedStep = stepUpdater.updateCurrentStep(testStep, testDto);

            MatcherAssert.assertThat(updatedStep, instanceOf(Step.class));
        }

        @Test
        @DisplayName("should update stepCount of Step object")
        public void testUpdateCurrentStep_UpdatesStepCount() {
            var upDatedStep = stepUpdater.updateCurrentStep(testStep, testDto);

            var expected = 169;

            assertEquals(expected, upDatedStep.getStepCount());
        }

        @Test
        @DisplayName("should update end time of Step object")
        public void testUpdateCurrentStep_UpdatesEndTime() {
            var upDatedStep = stepUpdater.updateCurrentStep(testStep, testDto);

            assertEquals(upDatedStep.getEndTime(), END_TIME);
        }
        @Test
        @DisplayName("should update upload time of Step object")
        public void testUpdateCurrentStep_UpdatesUploadTime() {
            var upDatedStep = stepUpdater.updateCurrentStep(testStep, testDto);

            assertEquals(upDatedStep.getUploadedTime(), UPLOADED_TIME);
        }
    }

    @Nested
    @DisplayName("updateStepCountForStep should return ")
    class UpdateStepCountForStepShouldReturnTest {

        @Test
        @DisplayName("object of BoostAppStep class")
        public void testUpdateStepCountForStep_ReturnsBoostAppStep() {
            var updatedStep = stepUpdater.updateStepCountForStep(testStep, testDto);

            assertNotNull(updatedStep);
            MatcherAssert.assertThat(updatedStep, instanceOf(BoostAppStep.class));
        }

        @Test
        @DisplayName("object of Step class when input is Step")
        public void testUpdateStepCountForStep_ReturnsStep_WhenInputIsStep() {
            var updatedStep = stepUpdater.updateStepCountForStep(testStep, testDto);

            assertNotNull(updatedStep);
            MatcherAssert.assertThat(updatedStep, instanceOf(Step.class));
        }

        @Test
        @DisplayName("object of MonthStep class when input is MonthStep")
        public void testUpdateStepCountForStep_ReturnsMonthStep_WhenInputIsMonthStep() {
            var updatedMonthStep = stepUpdater.updateStepCountForStep(testMonthStep, testDto);

            assertNotNull(updatedMonthStep);
            MatcherAssert.assertThat(updatedMonthStep, instanceOf(MonthStep.class));
        }

        @Test
        @DisplayName("object of MonthStep class when input is MonthStep")
        public void testUpdateStepCountForStep_ReturnsWeekStep_WhenInputIsWeekStep() {
            var updatedWeekStep = stepUpdater.updateStepCountForStep(testWeekStep, testDto);

            assertNotNull(updatedWeekStep);
            MatcherAssert.assertThat(updatedWeekStep, instanceOf(WeekStep.class));
        }
    }

    @Nested
    @DisplayName("updateStepCountForStep should update ")
    class UpdateStepCountForStepShouldUpdateTest {

        @Test
        @DisplayName("step count of Step")
        public void testUpdateStepCountForStep_UpdatesStepStepCount() {
            var updatedStep = stepUpdater.updateStepCountForStep(testStep, testDto);

            var expectedResult = 169;

            assertEquals(expectedResult, updatedStep.getStepCount());
        }

        @Test
        @DisplayName("step count of MonthStep")
        public void testUpdateStepCountForStep_UpdatesMonthStepStepCount() {
            var updatedMonthStep = stepUpdater.updateStepCountForStep(testMonthStep, testDto);

            var expectedResult = 169;

            assertEquals(expectedResult, updatedMonthStep.getStepCount());
        }

        @Test
        @DisplayName("step count of WeekStep")
        public void testUpdateStepCountForStep_UpdatesWeekStepStepCount() {
            var updatedWeekStep = stepUpdater.updateStepCountForStep(testWeekStep, testDto);

            var expectedResult = 169;

            assertEquals(expectedResult, updatedWeekStep.getStepCount());
        }
    }
    @Test
    @DisplayName("updateStepCountForStep should return BoostAppStep if DTO parameter is invalid")
    public void testUpdateStepCountForStep_ReturnsOriginalStep_WhenDtoInputIsInvalid() {
        var invalidDto = new StepDTO(13, null, null, null);
        var updatedWeekStep = stepUpdater.updateStepCountForStep(testWeekStep, invalidDto);

        assertEquals(testWeekStep, updatedWeekStep);
    }

    @Test
    @DisplayName("updateStepCountForStep should return BoostAppStep if DTO date time are invalid")
    public void testUpdateStepCountForStep_ReturnsOriginalStep_WhenStepInputIsInvalid() {
        var invalidDto = new StepDTO(13, UPLOADED_TIME, START_TIME, END_TIME);
        var updatedStep = stepUpdater.updateStepCountForStep(testStep, invalidDto);

        assertEquals(testStep, updatedStep);
    }
}