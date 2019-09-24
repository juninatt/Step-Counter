package se.sigma.boostapp.boost_app_java.step;

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

	@Column(name = "start")
	private LocalDateTime start;

	@Column(name = "end")
	private LocalDateTime end;

	public Step() {

	}

	public Step(int stepCount, String start, String end) {
		this.stepCount = stepCount;
		this.start = LocalDateTime.parse(start);
		this.end = LocalDateTime.parse(end);
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
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

}
