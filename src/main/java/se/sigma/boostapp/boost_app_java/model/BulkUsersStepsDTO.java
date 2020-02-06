package se.sigma.boostapp.boost_app_java.model;

import java.util.List;

public class BulkUsersStepsDTO {
	
	private String userId;
	private List stepList;

	public BulkUsersStepsDTO(String userId, List stepList) {
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

	public List getStepList() {
		return stepList;
	}

	public void setStepList(List stepList) {
		this.stepList = stepList;
	}
}
