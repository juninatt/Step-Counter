package se.sigma.boostapp.boost_app_java.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;
import se.sigma.boostapp.boost_app_java.validator.DateTimeRange;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ApiModel(description = "All details about the step entity. ")
@DateTimeRange
public class StepDTO {
	/**
	 * Data for the steps
	 */

	@ApiModelProperty(notes = "step count")
	@Min(value = 1, message = "Step count must be greater than or equal to 1")
	private int stepCount;

	@ApiModelProperty(notes = "Start time")
	@NotNull(message = "Start time must not be null")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startTime;

	@ApiModelProperty(notes = "End time")
	@NotNull(message = "End time must not be null")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endTime;

	@ApiModelProperty(notes = "Uploaded")
	@NotNull(message = "Upload time must not be null")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime uploadedTime;

	public StepDTO() {

	}

	public StepDTO(int stepCount, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime uploadedTime) {
		this.stepCount = stepCount;
		this.startTime = startTime;
		this.endTime = endTime;
		this.uploadedTime = uploadedTime;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public LocalDateTime getUploadedTime() {
		return uploadedTime;
	}

	public void setUploadedTime(LocalDateTime uploadedSteps) {
		this.uploadedTime = uploadedSteps;
	}
}
