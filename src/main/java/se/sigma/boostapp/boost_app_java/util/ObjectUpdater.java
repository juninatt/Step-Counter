package se.sigma.boostapp.boost_app_java.util;

import org.springframework.stereotype.Component;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

@Component
public class ObjectUpdater {

    private static final ObjectUpdater INSTANCE = new ObjectUpdater();

    private ObjectUpdater() {
        // private constructor to prevent instantiation
    }

    public static ObjectUpdater getInstance() {
        return INSTANCE;
    }

    public Step updateExistingStep(Step currentStep, StepDTO stepData) {
        try {
            currentStep.setStepCount(currentStep.getStepCount() + stepData.getStepCount());
            currentStep.setEndTime(stepData.getEndTime());
            currentStep.setUploadedTime(stepData.getUploadTime());
        } catch (IllegalArgumentException | NullPointerException exception) {
            System.out.println("Error updating step: " + exception.getMessage());
        }
        return currentStep;
    }

    public MonthStep updateMonthStep(MonthStep currentStep, StepDTO stepData) {
        try {
            currentStep.setSteps(currentStep.getSteps() + stepData.getStepCount());
        } catch (IllegalArgumentException | NullPointerException exception) {
            System.out.println("Error updating month step: " + exception.getMessage());
        }
        return currentStep;
    }

    public WeekStep updateWeekStep(WeekStep currentStep, StepDTO stepData) {
        try {
            currentStep.setSteps(currentStep.getSteps() + stepData.getStepCount());
        } catch (IllegalArgumentException | NullPointerException exception) {
            System.out.println("Error updating week step: " + exception.getMessage());
        }
        return currentStep;
    }
}

