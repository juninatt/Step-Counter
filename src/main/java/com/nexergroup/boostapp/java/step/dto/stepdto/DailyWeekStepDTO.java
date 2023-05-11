package com.nexergroup.boostapp.java.step.dto.stepdto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Schema(description = "Step stepCount per day for specific week")
public class DailyWeekStepDTO {

    @Schema(description = "User id")
    private String userId;

    @Schema(description = "Number of the week")
    @Min(value = 1, message = "Week number cant be lower than 1")
    @Max(value = 52, message = "Week number cant be greater than 52")
    private int weekNumber;

    @Schema(description = "Total number of steps taken on monday")
    private int mondayStepCount;

    @Schema(description = "Total number of steps taken on tuesday")
    private int tuesdayStepCount;

    @Schema(description = "Total number of steps taken on wednesday")
    private int wednesdayStepCount;

    @Schema(description = "Total number of steps taken on thursday")
    private int thursdayStepCount;

    @Schema(description = "Total number of steps taken on friday")
    private int fridayStepCount;

    @Schema(description = "Total number of steps taken on saturday")
    private int saturdayStepCount;

    @Schema(description = "Total number of steps taken on sunday")
    private int sundayStepCount;

    public DailyWeekStepDTO(String userId, int weekNumber, List<Integer> stepCountPerDay) {
        this.userId = userId;
        this.weekNumber = weekNumber;
        this.mondayStepCount = stepCountPerDay.get(0);
        this.tuesdayStepCount = stepCountPerDay.get(1);
        this.wednesdayStepCount = stepCountPerDay.get(2);
        this.thursdayStepCount = stepCountPerDay.get(3);
        this.fridayStepCount = stepCountPerDay.get(4);
        this.saturdayStepCount = stepCountPerDay.get(5);
        this.sundayStepCount = stepCountPerDay.get(6);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getMondayStepCount() {
        return mondayStepCount;
    }

    public void setMondayStepCount(int mondayStepCount) {
        this.mondayStepCount = mondayStepCount;
    }

    public int getTuesdayStepCount() {
        return tuesdayStepCount;
    }

    public void setTuesdayStepCount(int tuesdayStepCount) {
        this.tuesdayStepCount = tuesdayStepCount;
    }

    public int getWednesdayStepCount() {
        return wednesdayStepCount;
    }

    public void setWednesdayStepCount(int wednesdayStepCount) {
        this.wednesdayStepCount = wednesdayStepCount;
    }

    public int getThursdayStepCount() {
        return thursdayStepCount;
    }

    public void setThursdayStepCount(int thursdayStepCount) {
        this.thursdayStepCount = thursdayStepCount;
    }

    public int getFridayStepCount() {
        return fridayStepCount;
    }

    public void setFridayStepCount(int fridayStepCount) {
        this.fridayStepCount = fridayStepCount;
    }

    public int getSaturdayStepCount() {
        return saturdayStepCount;
    }

    public void setSaturdayStepCount(int saturdayStepCount) {
        this.saturdayStepCount = saturdayStepCount;
    }

    public int getSundayStepCount() {
        return sundayStepCount;
    }

    public void setSundayStepCount(int sundayStepCount) {
        this.sundayStepCount = sundayStepCount;
    }

    @Override
    public String toString() {
        return "WeekStepDTO{" +
                "userId='" + userId + '\'' +
                ", mondayStepCount=" + mondayStepCount +
                ", tuesdayStepCount=" + tuesdayStepCount +
                ", wednesdayStepCount=" + wednesdayStepCount +
                ", thursdayStepCount=" + thursdayStepCount +
                ", fridayStepCount=" + fridayStepCount +
                ", saturdayStepCount=" + saturdayStepCount +
                ", sundayStepCount=" + sundayStepCount +
                '}';
    }
}
