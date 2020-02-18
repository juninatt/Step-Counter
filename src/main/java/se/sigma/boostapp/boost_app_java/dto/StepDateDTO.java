package se.sigma.boostapp.boost_app_java.dto;

import java.util.Date;

public class StepDateDTO {

    private Date date;
    private long steps;

    public StepDateDTO(Date date, long steps) {
        this.date = date;
        this.steps = steps;
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
}
