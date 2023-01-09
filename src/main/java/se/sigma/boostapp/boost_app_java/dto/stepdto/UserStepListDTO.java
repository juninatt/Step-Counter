package se.sigma.boostapp.boost_app_java.dto.stepdto;

import java.util.List;

public class UserStepListDTO {

	private final String userId;
	private final List<StepDateDTO> stepList;

	public UserStepListDTO(String userId, List<StepDateDTO> stepList) {
		this.userId = userId;
		this.stepList = stepList;
	}

	public String getUserId() {
		return userId;
	}

	public List<StepDateDTO> getStepList() {
		return stepList;
	}

	@Override
	public String toString() {
		return "UserStepListDTO{" +
				"userId='" + userId + '\'' +
				", stepList=" + stepList +
				'}';
	}
}
