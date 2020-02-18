package se.sigma.boostapp.boost_app_java.dto;

import java.util.List;

public class BulkUserStarPointsDTO {

    private String userId;
    private String activity;
    private List<StarPointDateDTO> starPointList;

    public BulkUserStarPointsDTO(String userId, String activity, List<StarPointDateDTO> starPointList) {
        this.userId = userId;
        this.activity = activity;
        this.starPointList = starPointList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public List<StarPointDateDTO> getStarPointList() {
        return starPointList;
    }

    public void setStarPointList(List<StarPointDateDTO> starPointList) {
        this.starPointList = starPointList;
    }
}
