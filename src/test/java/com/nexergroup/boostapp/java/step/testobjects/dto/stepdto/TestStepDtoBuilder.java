package com.nexergroup.boostapp.java.step.testobjects.dto.stepdto;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;

import java.time.LocalDateTime;

public class TestStepDtoBuilder {

    private final String testUser = "testUser";
    private final int thisYear = LocalDateTime.now().getYear();
    private final LocalDateTime firstMinuteOfYear = LocalDateTime.of(thisYear, 1, 1, 1, 1, 1);
    private final LocalDateTime secondMinuteOfYear = LocalDateTime.of(thisYear, 1, 1, 1, 2, 2);
    private final LocalDateTime thirdMinuteOfYear = LocalDateTime.of(thisYear, 1, 1, 1, 3, 3);

    public StepDTO createStepDTOWhereUserIdIsNull() {
        return new StepDTO(
                null,
                10,
                firstMinuteOfYear,
                secondMinuteOfYear,
                thirdMinuteOfYear
        );
    }
    public StepDTO createStepDTOWhereStartTimeIsNull() {
        return new StepDTO(
                testUser,
                10,
                null,
                secondMinuteOfYear,
                thirdMinuteOfYear
        );
    }
    public StepDTO createStepDTOWhereEndTimeIsNull() {
        return new StepDTO(
                testUser,
                10,
                firstMinuteOfYear,
                null,
                thirdMinuteOfYear
        );
    }
    public StepDTO createStepDTOWhereUploadTimeIsNull() {
        return new StepDTO(
                testUser,
                10,
                firstMinuteOfYear,
                secondMinuteOfYear,
                null
        );
    }

    public StepDTO createStepDTOWhereTimeFieldsAreIncompatible() {
        return new StepDTO(
                testUser,
                10,
                thirdMinuteOfYear,
                secondMinuteOfYear,
                firstMinuteOfYear
        );
    }

    public StepDTO createStepDTOOfFirstMinuteOfYear() {
        return new StepDTO(
                testUser,
                10,
                firstMinuteOfYear,
                firstMinuteOfYear.plusSeconds(10),
                thirdMinuteOfYear.plusSeconds(20));
    }

    public StepDTO createStepDTOOfSecondMinuteOfYear() {
        return new StepDTO(
                testUser,
                20,
                secondMinuteOfYear,
                secondMinuteOfYear.plusSeconds(10),
                secondMinuteOfYear.plusSeconds(20));
    }

    public StepDTO createStepDTOOfThirdMinuteOfYear() {
        return new StepDTO(
                testUser,
                30,
                thirdMinuteOfYear,
                thirdMinuteOfYear.plusSeconds(10),
                thirdMinuteOfYear.plusSeconds(20));
    }
}

