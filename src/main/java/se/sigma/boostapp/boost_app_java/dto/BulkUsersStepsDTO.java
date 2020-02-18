package se.sigma.boostapp.boost_app_java.dto;

import java.util.List;

public class BulkUsersStepsDTO {
	
	private String userId;
	private List<StepDateDTO> stepList;

	public BulkUsersStepsDTO(String userId, List<StepDateDTO> stepList) {
		this.userId = userId;
		this.stepList = stepList;
	}

	public BulkUsersStepsDTO() {}

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
}
