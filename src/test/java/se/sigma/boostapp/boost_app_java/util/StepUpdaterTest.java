package se.sigma.boostapp.boost_app_java.util;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName(" <== StepUpdater ==>")
public class StepUpdaterTest {

    private final LocalDateTime START_TIME = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
    private final LocalDateTime END_TIME = LocalDateTime.of(2023, 1, 1, 2, 1, 1);
    private final LocalDateTime UPLOADED_TIME = LocalDateTime.of(2023, 1, 1, 3, 1, 1);

    private StepDTO testDto;
    private Step testStep;
    private StepUpdater stepUpdater;

    @BeforeEach
    public void setUp() {
        stepUpdater = StepUpdater.getInstance();

        testDto = new StepDTO(69, START_TIME, END_TIME, UPLOADED_TIME);
        testStep = new Step("testStep", 100, END_TIME);
        int YEAR = 2023;
        int MONTH = 1;
        MonthStep testMonthStep = new MonthStep("testStep", MONTH, YEAR, 100);
        int WEEK = 1;
        WeekStep testWeekStep = new WeekStep("testStep", WEEK, YEAR, 100);
    }
    @Nested
    @DisplayName("updateCurrentStep ")
    class UpdateCurrentStepTest {

        @Test
        @DisplayName("should return object of Step class")
        public void testUpdateCurrentStep_ReturnsObjectOfStepClass() {
            var updatedStep = stepUpdater.updateStep(testStep, testDto);

            MatcherAssert.assertThat(updatedStep, instanceOf(Step.class));
        }

        @Test
        @DisplayName("should update stepCount of Step object")
        public void testUpdateCurrentStep_UpdatesStepCount() {
            var upDatedStep = stepUpdater.updateStep(testStep, testDto);

            var expected = 169;

            assertEquals(expected, upDatedStep.getStepCount());
        }

        @Test
        @DisplayName("should update end time of Step object")
        public void testUpdateCurrentStep_UpdatesEndTime() {
            var upDatedStep = stepUpdater.updateStep(testStep, testDto);

            assertEquals(upDatedStep.getEndTime(), END_TIME);
        }
        @Test
        @DisplayName("should update upload time of Step object")
        public void testUpdateCurrentStep_UpdatesUploadTime() {
            var upDatedStep = stepUpdater.updateStep(testStep, testDto);

            assertEquals(upDatedStep.getUploadedTime(), UPLOADED_TIME);
        }
    }
}