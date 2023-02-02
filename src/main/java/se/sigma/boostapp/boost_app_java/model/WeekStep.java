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
    private int stepCount;

    public WeekStep() {
    }

    public WeekStep(String userId, int week, int year, int stepCount) {
        this.userId = userId;
        this.week = week;
        this.year = year;
        this.stepCount = stepCount;
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
    @Override
    public void setStepCount(int stepCount) {
        // exist for database to find variable
        this.stepCount = stepCount;
    }
    @Override
    public int getStepCount() {
        // exist for database to find variable
        return stepCount;
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
