package com.nexergroup.boostapp.java.step.dto.starpointdto;

public class BulkUserStarPointsDTO {

    private String userId;
    private StarPointDateDTO starPoints;

    public BulkUserStarPointsDTO() {}

    public BulkUserStarPointsDTO(String userId, StarPointDateDTO starPoints) {
        this.userId = userId;
        this.starPoints = starPoints;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public StarPointDateDTO getStarPoints() {
        return starPoints;
    }

    public void setStarPoints(StarPointDateDTO starPoints) {
        this.starPoints = starPoints;
    }
}
