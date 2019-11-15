package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDate;
import java.util.List;

public class BulkUsersStepsDTO {
	
	private LocalDate startDate;
	private List<String> userList;
	
	public BulkUsersStepsDTO(LocalDate startDate, List<String> userList) {
		super();
		this.startDate = startDate;
		this.userList = userList;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public List<String> getUserList() {
		return userList;
	}

	public void setUserList(List<String> userList) {
		this.userList = userList;
	}
	

}
