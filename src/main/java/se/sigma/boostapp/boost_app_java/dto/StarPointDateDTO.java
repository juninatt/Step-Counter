package se.sigma.boostapp.boost_app_java.dto;

import java.util.Date;

public class StarPointDateDTO {

    private Date date;
    private long starPoints;

    public StarPointDateDTO(Date date, long starPoints) {
        this.date = date;
        this.starPoints = starPoints;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getStarPoints() {
        return starPoints;
    }

    public void setStarPoints(long starPoints) {
        this.starPoints = starPoints;
    }
}
