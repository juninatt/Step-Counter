package com.nexergroup.boostapp.java.step.dto.starpointdto;

public class StarPointDateDTO {

    private String activity;

    private String description;

    private String startTime;

    private String endTime;

    private int starPoints;

    public StarPointDateDTO() {
    }

    public StarPointDateDTO(String activity, String description, String startTime, String endTime, int starPoints) {
        this.activity = activity;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.starPoints = starPoints;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getStarPoints() {
        return starPoints;
    }

    public void setStarPoints(int starPoints) {
        this.starPoints = starPoints;
    }
}
