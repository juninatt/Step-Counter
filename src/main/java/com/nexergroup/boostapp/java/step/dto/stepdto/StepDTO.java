package com.nexergroup.boostapp.java.step.dto.stepdto;

import com.nexergroup.boostapp.java.step.validator.DateTimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Schema(description = "All details about the step entity. ")
@DateTimeRange
public class StepDTO {

    /**
     * Data for the steps
     */

    @Schema(description = "User Id")
    private String userId;
    @Schema(description = "step count")
    @Min(value = 1, message = "Step count must be greater than or equal to 1")
    @NotNull(message = "Step count must not be null")
    private int stepCount;

    @Schema(description = "Start time")
    @NotNull(message = "Start time must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startTime;

    @Schema(description = "End time")
    @NotNull(message = "End time must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endTime;

    @Schema(description = "Upload time")
    @NotNull(message = "Upload time must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime uploadTime;

    public StepDTO() {
    }

    public StepDTO(String userId, ZonedDateTime uploadTime) {
        this.userId = userId;
        this.uploadTime = uploadTime;
    }

    public StepDTO(String userId, int stepCount, ZonedDateTime startTime, ZonedDateTime endTime, ZonedDateTime uploadTime) {
        this.userId = userId;
        this.stepCount = stepCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.uploadTime = uploadTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public void setUploadTime(ZonedDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public ZonedDateTime getUploadTime() {
        return uploadTime;
    }


    @Override
    public String toString() {
        return "StepDTO{" +
                "stepCount=" + stepCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", uploadTime=" + uploadTime +
                '}';
    }
}
