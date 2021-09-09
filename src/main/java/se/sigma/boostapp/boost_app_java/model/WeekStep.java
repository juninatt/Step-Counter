package se.sigma.boostapp.boost_app_java.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;


@Entity
@Table(name = "weekstep")
public class WeekStep {
    /**
     * Entity weekstep table
     */
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

    public WeekStep() {
    }

    public WeekStep(String userId, int week, int year, int steps) {
        this.userId = userId;
        this.week = week;
        this.year = year;
        this.steps = steps;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
