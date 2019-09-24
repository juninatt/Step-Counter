package se.sigma.boostapp.boost_app_java.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name ="step")
public class Step {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "step_count")
	private int stepCount;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	public Step() {

	}

	public Step(int stepCount, String start, String end) {
		this.stepCount = stepCount;
		this.startTime = LocalDateTime.parse(start);
		this.endTime = LocalDateTime.parse(end);
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
