package com.nexergroup.boostapp.java.step.builder;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;

import java.time.ZonedDateTime;

public class StepDTOBuilder extends BaseDTOBuilder<StepDTO> {

    private String userId;
    private int stepCount;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private ZonedDateTime uploadTime;

    public StepDTOBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public StepDTOBuilder withStepCount(int stepCount) {
        this.stepCount = stepCount;
        return this;
    }

    public StepDTOBuilder withStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public StepDTOBuilder withEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public StepDTOBuilder withUploadTime(ZonedDateTime uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }

    @Override
    protected StepDTO createDto() {
        return new StepDTO();
    }

    @Override
    public StepDTO build() {
        StepDTO stepDTO = super.build();
        stepDTO.setUserId(userId);
        stepDTO.setStepCount(stepCount);
        stepDTO.setStartTime(startTime);
        stepDTO.setEndTime(endTime);
        stepDTO.setUploadTime(uploadTime);
        return stepDTO;
    }
}
