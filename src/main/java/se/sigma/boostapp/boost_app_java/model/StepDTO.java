package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All details about the step entity. ")
public class StepDTO {
	
	@ApiModelProperty(notes = "step count")
	private int stepCount;
	
	@ApiModelProperty(notes = "User Id")
	private int userId;
	
	@ApiModelProperty(notes = "Start time")
	private LocalDateTime startTime;
	
	@ApiModelProperty(notes = "End time")
	private LocalDateTime endTime;
	
	
	public StepDTO() {
		
	}
	public StepDTO(int userId, int stepCount, String start, String end) {
		this.userId = userId;
		this.stepCount = stepCount;
		this.startTime = LocalDateTime.parse(start);
		this.endTime = LocalDateTime.parse(end);
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
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
	
}
