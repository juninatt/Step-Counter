package com.nexergroup.boostapp.java.step.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@DynamicUpdate
@Table(name = "monthstep")
public class MonthStep implements BoostAppStep{

    /**
     * Entity monthstep table
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The database generated monthStep ID")
    private long id;

    @Column(name = "user_id")
    @Schema(description = "userId")
    private String userId;

    @Column(name = "month")
    @Schema(description = "month")
    private int month;

    @Column(name = "year")
    @Schema(description = "year")
    private int year;

    @Column(name = "steps")
    private int stepCount;

    public MonthStep() {
    }

    public MonthStep(String userId, int month, int year, int stepCount) {
        this.userId = userId;
        this.month = month;
        this.year = year;
        this.stepCount = stepCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }
    @Override
    public String toString() {
        return "MonthStep{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", steps=" + stepCount +
                '}';
    }
}
