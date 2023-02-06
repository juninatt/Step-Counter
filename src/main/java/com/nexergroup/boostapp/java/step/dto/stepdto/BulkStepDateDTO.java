package com.nexergroup.boostapp.java.step.dto.stepdto;

import java.util.List;

public class BulkStepDateDTO {

	private String userId;
	private List<StepDateDTO> stepList;

	public BulkStepDateDTO() {
	}

	public BulkStepDateDTO(String userId, List<StepDateDTO> stepList) {
		this.userId = userId;
		this.stepList = stepList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<StepDateDTO> getStepList() {
		return stepList;
	}

	public void setStepList(List<StepDateDTO> stepList) {
		this.stepList = stepList;
	}

	@Override
	public String toString() {
		return "UserStepListDTO{" +
				"userId='" + userId + '\'' +
				", stepList=" + stepList +
				'}';
	}
}

