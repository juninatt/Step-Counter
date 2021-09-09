package se.sigma.boostapp.boost_app_java.dto;


import java.util.Date;

public class StepDateDTO {

    private Date date;
    private long steps;
    private String userId;
    private int dayOfWeek;

    public StepDateDTO(String userId, Date date, int dayOfWeek, long steps) {
        this.steps = steps;
        this.userId = userId;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
    }

    public StepDateDTO(Date date, long steps) {
        this.date = date;
        this.steps = steps;
    }

    public StepDateDTO(String userId, long steps) {
        this.userId = userId;
        this.steps = steps;
    }

    public StepDateDTO(String userId, Date date, long steps) {
        this.date = date;
        this.steps = steps;
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
