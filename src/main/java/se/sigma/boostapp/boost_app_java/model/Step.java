package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "step")
@ApiModel(description = "All details about the step entity. ")
public class Step {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated step ID")
	private long id;

	@Column(name = "user_id")
	@ApiModelProperty(notes = "User Id")
	private int userId;
	
	@ApiModelProperty(notes = "Step count")
	@Column(name = "step_count")
	private int stepCount;
	
	@ApiModelProperty(notes = "Start time")
	@Column(name = "start_time")
	private LocalDateTime startTime;
	
	@ApiModelProperty(notes = "End time")
	@Column(name = "end_time")
	private LocalDateTime endTime;

	public Step() {

	}

	public Step(int userId, int stepCount, String start, String end) {
		this.userId = userId;
		this.stepCount = stepCount;
		this.startTime = LocalDateTime.parse(start);
		this.endTime = LocalDateTime.parse(end);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}

	public LocalDateTime getStart() {
		return startTime;
	}

	public void setStart(LocalDateTime start) {
		this.startTime = start;
	}

	public LocalDateTime getEnd() {
		return endTime;
	}

	public void setEnd(LocalDateTime end) {
		this.endTime = end;
	}

}
