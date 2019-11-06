package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDateTime;
import java.util.List;

public class BulkDTO {
	
	private LocalDateTime startTime;
	
	private List<Integer> userList;
	
	public BulkDTO(String startTime, List<Integer> userIdList) {
		this.startTime = LocalDateTime.parse(startTime);
		this.userList = userIdList;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public List<Integer> getUserList() {
		return userList;
	}

	public void setUserList(List<Integer> userIdList) {
		this.userList = userIdList;
	}
	
	
}
