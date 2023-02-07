package com.nexergroup.boostapp.java.step.util;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.model.BoostAppStep;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;

import javax.validation.constraints.NotNull;

/**
 * A utility class for updating step-related objects.
 * This class provides the functionality of updating Step, MonthStep, and WeekStep with StepDTO.
 * The main method of this class is 'update(BoostAppStep, StepDTO)'. It takes a BoostAppStep object and a StepDTO object as input.
 * If the BoostAppStep object is of type Step, it will update the step count, end time and uploaded time of the Step object.
 * If the BoostAppStep object is of type WeekStep or MonthStep, it will update the steCount of the object.
 * This class follows the singleton pattern, use 'getInstance()' to get the instance of the class.
 *
 * @see BoostAppStep
 * @see Step
 * @see WeekStep
 * @see MonthStep
 * @see StepDTO
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

    public static StepUpdater getInstance() {
        return INSTANCE;
    }

    /**
     * Update {@link BoostAppStep} object.
     *
     * @param <T> represents the object implementing {@link BoostAppStep}
     * @param step the BoostAppStep object to be updated. It can be an instance of Step, WeekStep or MonthStep
     * @param newStepData the {@link StepDTO} object that contains the new Step data
     * @return the updated BoostAppStep object
     * @throws IllegalArgumentException if {@link BoostAppStep} or {@link StepDTO} is null
     */
    public <T extends BoostAppStep> T update(@NotNull T step, @NotNull StepDTO newStepData) {
        if (step instanceof Step)
            return (T) updateStep((Step) step, newStepData);
        else {
            return updateStepCount(step, newStepData.getStepCount());
        }
    }

    /**
     * Update a {@link Step} object with new step data.
     *
     * @param step the {@link Step} object to update
     * @param newStepData the {@link StepDTO} object
     * @return the updated Step object
     * @throws IllegalArgumentException if there is an error updating the Step object
     */
    private Step updateStep(Step step, StepDTO newStepData) {
        try {
            step.setStepCount(step.getStepCount() + newStepData.getStepCount());
            step.setEndTime(newStepData.getEndTime());
            step.setUploadedTime(newStepData.getUploadTime());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalArgumentException("Error updating step: " + exception.getMessage(), exception);
        }
        return step;
    }

    /**
     * Update the step count of a {@link WeekStep} or
     * {@link MonthStep} object.
     *
     * @param <T> represents the object implementing {@link BoostAppStep}
     * @param step the BoostAppStep object to update. It can be an instance of either WeekStep or MonthStep
     * @param newSteps the new stepCount
     * @return the updated {@link BoostAppStep} object
     * @throws IllegalArgumentException if there is an error updating the step count
     */
    private  <T extends BoostAppStep> T updateStepCount(T step, int newSteps) {
        try {
            step.setStepCount(step.getStepCount() + newSteps);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalArgumentException("Error updating step count: " + exception.getMessage(), exception);
        }
        return step;
    }
}
