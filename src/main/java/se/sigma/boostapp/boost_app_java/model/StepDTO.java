package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDateTime;

public class StepDTO {
	
	private int stepCount;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	
	public StepDTO(int stepCount, String start, String end) {
		this.stepCount = stepCount;
		this.startTime = LocalDateTime.parse(start);
		this.endTime = LocalDateTime.parse(end);
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
