package se.sigma.boostapp.boost_app_java.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicUpdate;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@DynamicUpdate
@Table(name="monthstep")
public class MonthStep {
/**
 * Entiti monthstep table
 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated monthStep ID")
	private long id;
	
	@Column(name = "user_id")
	@ApiModelProperty(notes = "userId")
    private String userId;
	
	@Column(name = "month")
	@ApiModelProperty(notes = "month")
	private int month;
	
	@Column(name = "year")
	//@ApiModelProperty(notes = "year")
    private int year;

	@Column(name = "steps")
	private int steps;

	public MonthStep(String userId, int month, int year, int steps) {
		this.userId = userId;
		this.month = month;
		this.year = year;
		this.steps = steps;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}


}