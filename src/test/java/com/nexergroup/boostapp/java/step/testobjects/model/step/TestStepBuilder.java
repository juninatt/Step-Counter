package com.nexergroup.boostapp.java.step.testobjects.model.step;

import com.nexergroup.boostapp.java.step.model.Step;

import java.time.LocalDateTime;

public class TestStepBuilder {

    private final String testUser = "testUser";
    private final int thisYear = LocalDateTime.now().getYear();
    private final LocalDateTime firstMinuteOfYear = LocalDateTime.of(thisYear, 1, 1, 1, 1, 1);
    private final LocalDateTime secondMinuteOfYear = LocalDateTime.of(thisYear, 1, 1, 1, 2, 2);
    private final LocalDateTime thirdMinuteOfYear = LocalDateTime.of(thisYear, 1, 1, 1, 3, 3);

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
                thirdMinuteOfYear.plusSeconds(20));
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
}
