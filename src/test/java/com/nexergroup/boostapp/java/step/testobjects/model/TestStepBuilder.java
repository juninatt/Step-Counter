package com.nexergroup.boostapp.java.step.testobjects.model;

import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TestStepBuilder {

    private final String testUser = "testUser";
    private final int thisYear = LocalDateTime.now().getYear();
    private final ZonedDateTime firstMinuteOfYear =
            LocalDateTime.of(thisYear, 1, 1, 1, 1, 1).atZone(ZoneId.systemDefault());
    private final ZonedDateTime secondMinuteOfYear =
            LocalDateTime.of(thisYear, 1, 1, 1, 2, 2).atZone(ZoneId.systemDefault());
    private final ZonedDateTime thirdMinuteOfYear =
            LocalDateTime.of(thisYear, 1, 1, 1, 3, 3).atZone(ZoneId.systemDefault());

    public Step createStepWhereUserIdIsNull() {
        return new Step(
                null,
                10,
                firstMinuteOfYear,
                secondMinuteOfYear,
                thirdMinuteOfYear
        );
    }

    public Step createStepOfFirstMinuteOfYear() {
        return new Step(
                testUser,
                10,
                firstMinuteOfYear,
                firstMinuteOfYear.plusSeconds(10),
                firstMinuteOfYear.plusSeconds(20));
    }

    public Step createStepOfSecondMinuteOfYear() {
        return new Step(
                testUser,
                20,
                secondMinuteOfYear,
                secondMinuteOfYear.plusSeconds(10),
                secondMinuteOfYear.plusSeconds(20));
    }

    public Step createStepOfThirdMinuteOfYear() {
        return new Step(
                testUser,
                30,
                thirdMinuteOfYear,
                thirdMinuteOfYear.plusSeconds(10),
                thirdMinuteOfYear.plusSeconds(20));
    }

    public WeekStep createWeekStepOfStep(Step step) {
        return new WeekStep(testUser, DateHelper.getWeek(step.getEndTime()), step.getEndTime().getYear(), step.getStepCount());
    }

    public WeekStep createWeekStepOfFirstWeekOfYear() {
        return new WeekStep(testUser, 1, thisYear, 10);
    }

    public MonthStep createMonthStepOfStep(Step step) {
        return new MonthStep(testUser, step.getEndTime().getMonthValue(), step.getEndTime().getYear(), step.getStepCount());
    }

    public MonthStep createMonthStepOfFirstMonthOfYear() {
        return new MonthStep(testUser, 1, thisYear, 10);
    }
}
