package se.sigma.boostapp.boost_app_java.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;


@Entity
@Table(name = "weekstep")
public class WeekStep implements BoostAppStep {

    /**
     * Entity weekstep table
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The database generated weekStep ID")
    private long id;

    @Column(name = "user_id")
    @Schema(description = "User Id")
    private String userId;

    @Column(name = "week")
    private int week;

    @Schema(description = "Year")
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
        // Exist for mapper to find variable. Rename steps-variable to 'stepCount'
        return steps;
    }

    public void setSteps(int steps) {
        // Exist for mapper to find variable. Rename steps-variable to 'stepCount'
        this.steps = steps;
    }
    @Override
    public void setStepCount(int stepCount) {
        // exist for database to find variable
        this.steps = stepCount;
    }
    @Override
    public int getStepCount() {
        // exist for database to find variable
        return steps;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {

        return userId;
    }
}
