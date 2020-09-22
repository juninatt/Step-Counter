package se.sigma.boostapp.boost_app_java.model;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="stepweek")
public class WeekStep {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@ApiModelProperty(notes = "The database generated weekStep ID")
		private long id;
		
		@Column(name = "user_id")
		@ApiModelProperty(notes = "User Id")
	    private String userId;

        @Column(name = "week")
        private int week;

		@ApiModelProperty(notes = "Year")
		@Column(name = "year")
	    private int year;

		@Column(name = "steps")
        private int steps;


	public WeekStep(String userId, int week, int year, int steps) {
		this.userId = userId;
		this.week = week;
		this.year = year;
		this.steps = steps;
	}
}
