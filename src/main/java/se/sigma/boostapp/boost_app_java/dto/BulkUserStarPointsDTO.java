package se.sigma.boostapp.boost_app_java.dto;

import java.util.List;

public class BulkUserStarPointsDTO {

    private String userId;
    private List<StarPointDateDTO> starPointList;

    public BulkUserStarPointsDTO(String userId, List<StarPointDateDTO> starPointList) {
        this.userId = userId;
        this.starPointList = starPointList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<StarPointDateDTO> getStarPointList() {
        return starPointList;
    }

    public void setStarPointList(List<StarPointDateDTO> starPointList) {
        this.starPointList = starPointList;
    }
}
