package se.sigma.boostapp.boost_app_java.dto.stepdto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import se.sigma.boostapp.boost_app_java.validator.DateTimeRange;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "All details about the step entity. ")
@DateTimeRange
public class StepDTO {

    /**
     * Data for the steps
     */
    @Schema(description = "step count")
    @Min(value = 1, message = "Step count must be greater than or equal to 1")
    @NotNull(message = "Step count must not be null")
    private int stepCount;

    @Schema(description = "Start time")
    @NotNull(message = "Start time must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @Schema(description = "End time")
    @NotNull(message = "End time must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @Schema(description = "Upload time")
    @NotNull(message = "Upload time must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime uploadTime;

    public StepDTO() {
    }

    public StepDTO(int stepCount, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime uploadTime) {
        this.stepCount = stepCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.uploadTime = uploadTime;
    }

    public int getStepCount() {
        return stepCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public int getYear() {
        return endTime.getYear();
    }
    public int getMonth() {
        return endTime.getMonthValue();
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
