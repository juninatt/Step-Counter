package se.sigma.boostapp.boost_app_java.util;

import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.BoostAppStep;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;

import javax.validation.constraints.NotNull;

/**
 * A utility class for updating step-related objects.
 * This class provide the functionality of updating step, monthStep and weekStep with stepDTO
 *
 */
public class StepUpdater {

    private static final StepUpdater INSTANCE = new StepUpdater();

    /**
     * A private constructor to prevent instantiation of this class.
     *
     */
    private StepUpdater() {
        // private constructor to prevent instantiation
    }

    /**
     * Gets the singleton instance of this class.
     *
     * @return the singleton instance of this class.
     */
    public static StepUpdater getInstance() {
        return INSTANCE;
    }


    /**
     * Updates an existing step with the data from a step DTO.
     *
     * @param currentStep the {@link Step} object to update
     * @param newStepData the {@link StepDTO} object to update the step with
     * @return the updated step
     */
    public Step updateCurrentStep(@NotNull Step currentStep, @NotNull StepDTO newStepData) {
        return (Step) updateStepCount(updateTime(currentStep, newStepData), newStepData);
    }


    /**
     * Updates an existing MonthStep with the data from a step DTO.
     *
     * @param currentStep the {@link MonthStep} object to update
     * @param newStepData the {@link StepDTO} object to update the step with
     * @return the updated MonthStep
     */
    public BoostAppStep updateStepCountForStep(@NotNull BoostAppStep currentStep, @NotNull StepDTO newStepData) {
        return updateStepCount(currentStep, newStepData);
    }


    /**
     * Updates the step count of a BoostAppStep object.
     *
     * @param currentStep the BoostAppStep object to update
     * @param newStepData the StepDTO object containing the updated step count
     * @return the updated BoostAppStep object
     */
    private BoostAppStep updateStepCount(BoostAppStep currentStep, StepDTO newStepData) {
        try {
            currentStep.setStepCount(currentStep.getStepCount() + newStepData.getStepCount());
        } catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return currentStep;
    }

    /**
     * Updates the end time and uploaded time of a Step object.
     *
     * @param currentStep the Step object to update
     * @param newStepData the StepDTO object containing the updated time information
     * @return the updated Step object
     */
    private Step updateTime(Step currentStep, StepDTO newStepData) {
        try {
            currentStep.setEndTime(newStepData.getEndTime());
            currentStep.setUploadedTime(newStepData.getUploadTime());
        } catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return currentStep;
    }
}

