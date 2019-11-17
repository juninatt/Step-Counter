package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDate;
import java.util.List;

public class BulkUsersStepsDTO {
	
	private LocalDate startDate;
	private LocalDate endDate;
	private List<String> userList;
	
	public BulkUsersStepsDTO(LocalDate startDate, LocalDate endDate, List<String> userList) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.userList = userList;
	}
	
	public BulkUsersStepsDTO(LocalDate startDate, List<String> userList) {
		this.startDate = startDate;
		this.endDate = LocalDate.now();
		this.userList = userList;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public List<String> getUserList() {
		return userList;
	}

	public void setUserList(List<String> userList) {
		this.userList = userList;
	}

}
