package se.sigma.boostapp.boost_app_java.util;

import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

import javax.validation.constraints.NotNull;

/**
 * A utility class for updating step-related objects.
 * This class provide the functionality of updating step, monthStep and weekStep with stepDTO
 *
 */
public class ObjectUpdater {

    private static final ObjectUpdater INSTANCE = new ObjectUpdater();

    /**
     * A private constructor to prevent instantiation of this class.
     *
     */
    private ObjectUpdater() {
        // private constructor to prevent instantiation
    }

    /**
     * Gets the singleton instance of this class.
     *
     * @return the singleton instance of this class.
     */
    public static ObjectUpdater getInstance() {
        return INSTANCE;
    }


    /**
     * Updates an existing step with the data from a step DTO.
     *
     * @param currentStep the {@link Step} object to update
     * @param stepData the {@link StepDTO} object to update the step with
     * @return the updated step
     */
    public Step updateExistingStep(@NotNull Step currentStep,@NotNull StepDTO stepData) {
        try {
            currentStep.setStepCount(currentStep.getStepCount() + stepData.getStepCount());
            currentStep.setEndTime(stepData.getEndTime());
            currentStep.setUploadedTime(stepData.getUploadTime());
        } catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return currentStep;
    }

    /**
     * Updates an existing MonthStep with the data from a step DTO.
     *
     * @param currentStep the {@link MonthStep} object to update
     * @param stepData the {@link StepDTO} object to update the step with
     * @return the updated MonthStep
     */
    public MonthStep updateMonthStep(MonthStep currentStep, StepDTO stepData) {
        try {
            currentStep.setSteps(currentStep.getSteps() + stepData.getStepCount());
        } catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return currentStep;
    }

    /**
     * Updates an existing WeekStep with the data from a step DTO.
     *
     * @param currentStep the {@link WeekStep} object to update
     * @param stepData the {@link StepDTO} object to update the step with
     * @return the updated WeekStep
     */
    public WeekStep updateWeekStep(WeekStep currentStep, StepDTO stepData) {
        try {
            currentStep.setSteps(currentStep.getSteps() + stepData.getStepCount());
        } catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return currentStep;
    }
}

