package se.sigma.boostapp.boost_app_java.model;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@DynamicUpdate
@Table(name = "monthstep")
public class MonthStep {
    /**
     * Entity monthstep table
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
    @ApiModelProperty(notes = "year")
    private int year;

    @Column(name = "steps")
    private int steps;

    public MonthStep() {
    }

    public MonthStep(String userId, int month, int year, int steps) {
        this.userId = userId;
        this.month = month;
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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