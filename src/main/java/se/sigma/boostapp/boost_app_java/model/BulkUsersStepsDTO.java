package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDateTime;
import java.util.List;

public class BulkUsersStepsDTO {
	
	private LocalDateTime startTime;
	
	private List<String> userList;
	
	public BulkUsersStepsDTO(String startTime, List<String> userIdList) {
		this.startTime = LocalDateTime.parse(startTime);
		this.userList = userIdList;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public List<String> getUserList() {
		return userList;
	}

	public void setUserList(List<String> userIdList) {
		this.userList = userIdList;
	}
	
	
}
