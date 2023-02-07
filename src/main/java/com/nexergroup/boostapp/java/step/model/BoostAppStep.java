package com.nexergroup.boostapp.java.step.model;

/**
 * This interface defines the methods to set and get the userId and stepCount fields of the implementing classes
 *
 * @see Step
 * @see WeekStep
 * @see MonthStep
 */
public interface BoostAppStep {

    /**
     * Set the value for userId from the BoostAppStep.
     *
     * @param userId the value to set the userId to
     */
    void setUserId(String userId);

    /**
     * Get the value for userId from the BoostAppStep.
     *
     * @return the userId value
     */
    String getUserId();

    /**
     * Set the value for stepCount from the BoostAppStep.
     *
     * @param stepCount the value to set the stepCount to
     */
    void setStepCount(int stepCount);

    /**
     * Get the value for stepCount from the BoostAppStep.
     *
     * @return the stepCount value
     */
    int getStepCount();

}

