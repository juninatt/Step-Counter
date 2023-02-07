package com.nexergroup.boostapp.java.step.util;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.model.Step;

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
    }
    @Nested
    @DisplayName("updateCurrentStep ")
    class UpdateCurrentStepTest {

        @Test
        @DisplayName("should return object of Step class")
        public void testUpdateCurrentStep_ReturnsObjectOfStepClass() {
            var updatedStep = stepUpdater.update(testStep, testDto);

            MatcherAssert.assertThat(updatedStep, instanceOf(Step.class));
        }

        @Test
        @DisplayName("should update stepCount of Step object")
        public void testUpdateCurrentStep_UpdatesStepCount() {
            var upDatedStep = stepUpdater.update(testStep, testDto);

            var expected = 169;

            assertEquals(expected, upDatedStep.getStepCount());
        }

        @Test
        @DisplayName("should update end time of Step object")
        public void testUpdateCurrentStep_UpdatesEndTime() {
            var upDatedStep = stepUpdater.update(testStep, testDto);

            assertEquals(upDatedStep.getEndTime(), END_TIME);
        }
        @Test
        @DisplayName("should update upload time of Step object")
        public void testUpdateCurrentStep_UpdatesUploadTime() {
            var upDatedStep = stepUpdater.update(testStep, testDto);

            assertEquals(upDatedStep.getUploadedTime(), UPLOADED_TIME);
        }
    }
}
