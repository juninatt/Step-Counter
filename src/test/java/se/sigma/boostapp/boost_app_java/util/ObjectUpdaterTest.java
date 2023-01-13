package se.sigma.boostapp.boost_app_java.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName(" <== ObjectUpdater ==>")
public class ObjectUpdaterTest {

    private static final LocalDateTime START_TIME = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2023, 1, 1, 2, 1, 1);
    private static final LocalDateTime UPLOADED_TIME = LocalDateTime.of(2023, 1, 1, 3, 1, 1);
    private static final int WEEK = 1;
    private static final int MONTH = 1;
    private static final int YEAR = 2023;

    private static StepDTO testDto;
    private static Step testStep;
    private static MonthStep testMonthStep;
    private static WeekStep testWeekStep;
    private static ObjectUpdater objectUpdater;

    @BeforeAll
    public static void setUp() {
        objectUpdater = ObjectUpdater.getInstance();

        testDto = new StepDTO(69, START_TIME, END_TIME, UPLOADED_TIME);
        testStep = new Step("testStep", 100, END_TIME);
        testMonthStep = new MonthStep("testStep", MONTH, YEAR, 100);
        testWeekStep = new WeekStep("testStep", WEEK, YEAR, 100);
    }

    @Nested
    @DisplayName("Updates step type: ")
    public class UpdaterShouldUpdate {

        @Test
        @DisplayName("existing step correctly")
        public void testUpdateExistingStep() {
            var expectedResult = 169;
            Step updatedStep = objectUpdater.updateExistingStep(testStep, testDto);
            assertNotNull(updatedStep);
            assertEquals(END_TIME, updatedStep.getEndTime());
            assertEquals(UPLOADED_TIME, updatedStep.getUploadedTime());
            assertEquals(expectedResult, updatedStep.getStepCount());
        }

        @Test
        @DisplayName("month-step correctly")
        public void testUpdateMonthStep() {
            var expectedResult = 169;
            MonthStep updatedMonthStep = objectUpdater.updateMonthStep(testMonthStep, testDto);
            assertNotNull(updatedMonthStep);
            assertEquals(expectedResult, updatedMonthStep.getSteps());
        }

        @Test
        @DisplayName("week-step correctly")
        public void testUpdateWeekStep() {
            var expectedResult = 169;
            WeekStep updatedWeekStep = objectUpdater.updateWeekStep(testWeekStep, testDto);
            assertNotNull(updatedWeekStep);
            assertEquals(expectedResult, updatedWeekStep.getSteps());
        }
    }
}